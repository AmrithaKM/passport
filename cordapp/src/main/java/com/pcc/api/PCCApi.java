package com.pcc.api;

import com.google.common.collect.ImmutableMap;
import com.pcc.bean.*;
import com.pcc.flows.*;
import com.pcc.states.PassportDataState;
import com.pcc.utility.PoliceCommonUtils;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.FlowException;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;

@Path("pcc")
public class PCCApi {
    static private final Logger logger = LoggerFactory.getLogger(PCCApi.class);
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    public PCCApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> myIdentity() {
        return ImmutableMap.of("me", rpcOps.nodeInfo().getLegalIdentities().get(0).getName());
    }

    /**
     * PASSPORT Code to Start here
     **/

    @GET
    @Path("getAllPassportDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PassportDataState>> getAllPassportDetails() {
        return rpcOps.vaultQuery(PassportDataState.class).getStates();
    }

    @GET
    @Path("getAllPassportDetailsPageWise")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PassportDataState>> getAllPassportDetailsPageWise(@QueryParam("pageNumber") Integer pageNumber,
                                                                              @QueryParam("maxPageSize") Integer maxPageSize) {
        PageSpecification pageSpec = new PageSpecification(pageNumber, maxPageSize);
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        return rpcOps.vaultQueryByWithPagingSpec(PassportDataState.class, queryCriteria, pageSpec).getStates();
    }

    @GET
    @Path("getPassportDataForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PassportDataState>> getPassportDataForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);
        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.UNCONSUMED,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, PassportDataState.class).getStates();
    }


    @GET
    @Path("trackPassportDataForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PassportDataState>> trackPassportDataForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);

        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.ALL,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, PassportDataState.class).getStates();
    }

    /**
     * This method creates a new PCC state in the DSBO node's vault DB ---- Step 1
     */
    @POST
    @Path("passportCreateData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportCreateData(PassportApplicationDetailsBean dataBean) {
        Party owner = rpcOps.partiesFromName("DSBO", false).iterator().next();
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            return Response.status(BAD_REQUEST).entity("Passport Application can only come through DSBO").build();
        }
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listenerDSBO = rpcOps.partiesFromName("DSBO", false).iterator().next();
        AbstractParty listenerDCRB = rpcOps.partiesFromName("DCRB", false).iterator().next();
        AbstractParty listenerFVO = rpcOps.partiesFromName("FVO", false).iterator().next();
        listOfListeners.add(listenerDSBO);
        listOfListeners.add(listenerDCRB);
        listOfListeners.add(listenerFVO);

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportCreateDataFlow.class,
                    owner,
                    dataBean,
                    listOfListeners).getReturnValue().get();

            System.out.println("\nPassportDataState created with transaction id: " + signedTx.getId()
                    + " and linear id: " + signedTx.getCoreTransaction()
                    .outputsOfType(PassportDataState.class).get(0).getLinearId());

            final String msg = String.format("%s\n",
                    signedTx.getCoreTransaction().outputsOfType(PassportDataState.class).get(0).getLinearId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method updates the physical verification flag in the state object, to be updated by DSBO and FVO ---- Step 2a
     */
    @POST
    @Path("passportUpdatePhysicalVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportUpdatePhysicalVerification(PassportUpdatePhysicalVerificatonBean dataBean) {
        System.out.println("passportUpdatePhysicalVerification 1");
        SecureHash secureHash = null;
        String partyName = "";
        if (rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DCRB")) {
            return Response.status(BAD_REQUEST).entity("Physical Verification details can be updated by either DSBO or FVO").build();
        }
        if (rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            partyName = "DSBO";
        }
        if (rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("FVO")) {
            partyName = "FVO";
        }
        System.out.println("passportUpdatePhysicalVerification 2");
        try {
            secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "imagePhysicalVerification"
                    + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("imagePhysicalVerification could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("imagePhysicalVerification secureHash file has some issue");
        }
        System.out.println("passportUpdatePhysicalVerification 3");
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listenerDSBO = rpcOps.partiesFromName("DSBO", false).iterator().next();
        AbstractParty listenerDCRB = rpcOps.partiesFromName("DCRB", false).iterator().next();
        AbstractParty listenerFVO = rpcOps.partiesFromName("FVO", false).iterator().next();
        listOfListeners.add(listenerDSBO);
        listOfListeners.add(listenerDCRB);
        listOfListeners.add(listenerFVO);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        String msg = "";
        try {
            if (null != dataBean.getFilePath() && !dataBean.getFilePath().trim().isEmpty()) {
                System.out.println("passportUpdatePhysicalVerification 4");
                SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportUpdatePhysicalVerificationFlow.class,
                        secureHash,
                        dataBean,
                        partyName,
                        listOfListeners,
                        linearId).getReturnValue().get();
                msg = signedTx.getId().toString();
            } else {
                System.out.println("passportUpdatePhysicalVerification 5");
                SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportUpdatePhysicalVerificationFlow.class,
                        dataBean,
                        partyName,
                        listOfListeners,
                        linearId).getReturnValue().get();
                msg = signedTx.getId().toString();
            }

            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method updates the criminal history in the state object ---- Step 2b
     */
    @POST
    @Path("passportUpdateCriminalHistory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportUpdateCriminalHistory(PassportUpdateCriminalHistoryBean dataBean) {
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DCRB")) {
            return Response.status(BAD_REQUEST).entity("Criminal details can be updated by DCRB only").build();
        }

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listenerDSBO = rpcOps.partiesFromName("DSBO", false).iterator().next();
        AbstractParty listenerDCRB = rpcOps.partiesFromName("DCRB", false).iterator().next();
        AbstractParty listenerFVO = rpcOps.partiesFromName("FVO", false).iterator().next();
        listOfListeners.add(listenerDSBO);
        listOfListeners.add(listenerDCRB);
        listOfListeners.add(listenerFVO);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        String msg = "";
        try {
            SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportUpdateCriminalHistoryFlow.class,
                    dataBean,
                    listOfListeners,
                    linearId).getReturnValue().get();
            msg = signedTx.getId().toString();
            return Response.status(CREATED).entity(msg).build();
        } catch (Throwable ex) {
            msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method checks if Commissioner has viewed the application ---- Step 4a
     */
    @POST
    @Path("passportCommissionerViewed")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportCommissionerViewed(PCCApproveOrRejectBean dataBean) {
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            return Response.status(BAD_REQUEST).entity("Only Commissioner in DSBO node can Approve/Reject Passport Application").build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        System.out.println("passportCommissionerViewed 1");
        String msg = "";

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportCommissionerViewedFlow.class,
                    linearId).getReturnValue().get();
            msg = signedTx.getId().toString();
            System.out.println("passportCommissionerViewed 2: " + msg);
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method Commissioner adds some notes to the application ---- Step 4b
     */
    @POST
    @Path("passportCommissionerNotes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportCommissionerNotes(PassportCommissionerNotesBean dataBean) {
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            return Response.status(BAD_REQUEST).entity("Only Commissioner in DSBO node can send notes").build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        System.out.println("passportCommissionerNotes 1");
        String msg = "";

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportCommissionerNotesFlow.class,
                    linearId,
                    dataBean.getCommissionerNotes(),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress()).getReturnValue().get();
            msg = signedTx.getId().toString();
            System.out.println("passportCommissionerViewed 2: " + msg);
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method finally approves or rejects with an image and description a PCC application ---- Step 4 c
     */
    @POST
    @Path("passportApproveOrReject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportApproveOrReject(PCCApproveOrRejectBean dataBean) {
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            return Response.status(BAD_REQUEST).entity("Only Commissioner in DSBO node can Approve/Reject Passport Application").build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listenerDSBO = rpcOps.partiesFromName("DSBO", false).iterator().next();
        AbstractParty listenerDCRB = rpcOps.partiesFromName("DCRB", false).iterator().next();
        AbstractParty listenerFVO = rpcOps.partiesFromName("FVO", false).iterator().next();
        listOfListeners.add(listenerDSBO);
        listOfListeners.add(listenerDCRB);
        listOfListeners.add(listenerFVO);

        SecureHash secureHash = null;
        if (StringUtil.isNotBlank(dataBean.getFilePath())) {
            try {
                secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "rejectionFile"
                        + Instant.now().getNano(), dataBean.getFilePath());
                if (null == secureHash) {
                    throw new FlowException("#rejectionFile could not be uploaded successfully#");
                }
            } catch (Exception e) {
                System.out.println("#passportApproveOrReject secureHash file has some issue#");
            }
        }
        System.out.println("passportApproveOrReject 1");
        String msg = "";
        try {
            if (secureHash != null) {
                System.out.println("passportApproveOrReject 2");
                final SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        secureHash,
                        listOfListeners,
                        linearId).getReturnValue().get();
                msg = signedTx.getId().toString();
            } else {
                System.out.println("passportApproveOrReject 3");
                final SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        listOfListeners,
                        linearId).getReturnValue().get();
                msg = signedTx.getId().toString();
            }

            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method saves the approved user's certificate to vault DB ---- Step 5
     */
    @POST
    @Path("passportSaveCertificate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportSaveCertificate(PCCSavePDFBean dataBean) {
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            return Response.status(BAD_REQUEST).entity("Only Commissioner in DSBO node can save certificate for Passport Application").build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        SecureHash secureHash = null;

        try {
            secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "certificate"
                    + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("certificate could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("passportSaveCertificate secureHash file has some issue");
        }

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(PassportSaveCertificateFlow.class,
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    secureHash,
                    linearId).getReturnValue().get();
            final String msg = signedTx.getId().toString();
            return Response.status(CREATED).entity(msg).build();
        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }
    /**PASSPORT Code to End here**/

}