package com.pcc.api;

import com.google.common.collect.ImmutableMap;
import com.pcc.bean.*;
import com.pcc.flows.*;
import com.pcc.states.NOCDataState;
import com.pcc.states.PCCDataState;
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
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static javax.ws.rs.core.Response.Status.*;

@Path("pccbackup")
public class PCCApiBackup {
    static private final Logger logger = LoggerFactory.getLogger(PCCApiBackup.class);
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    public PCCApiBackup(CordaRPCOps rpcOps) {
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
     * PCC Code Starts
     **/

    /**
     * This method retrieves all current PCC states in the corresponding node's vault DB
     */
    @GET
    @Path("getAllPCCDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PCCDataState>> getAllPCCDetails() {
        return rpcOps.vaultQuery(PCCDataState.class).getStates();
    }

    @GET
    @Path("getAllPCCDetailsPageWise")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PCCDataState>> getAllPCCDetailsPageWise(@QueryParam("pageNumber") Integer pageNumber,
                                                                    @QueryParam("maxPageSize") Integer maxPageSize) {
        PageSpecification pageSpec = new PageSpecification(pageNumber, maxPageSize);
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        return rpcOps.vaultQueryByWithPagingSpec(PCCDataState.class, queryCriteria, pageSpec).getStates();
    }


    /**
     * This method retrieves current PCC states in the corresponding node's vault DB for the specified Id
     */
    @GET
    @Path("getPCCDataForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PCCDataState>> getPCCDataForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);
        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.UNCONSUMED,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, PCCDataState.class).getStates();
    }


    /**
     * This method retrieves all current and historical PCC states in the corresponding node's vault DB for specified Id
     */
    @GET
    @Path("trackPCCDataForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PCCDataState>> trackPCCDataForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);

        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.ALL,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, PCCDataState.class).getStates();
    }

    /**
     * This method creates a new PCC state in the DSBO node's vault DB
     */
    @POST
    @Path("pccCreateData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccCreateData(PCCApplicationDetailsBean dataBean) {
        Party owner = rpcOps.partiesFromName("DSBO", false).iterator().next();
        InputStream inputStreamAddress = null;
        InputStream inputIdentity = null;

        SecureHash attachmentHashValueAddressProofImage = null;
        SecureHash attachmentHashValueIdentityProofImage = null;

        try {
            attachmentHashValueAddressProofImage =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "AddressProofImage"
                            + Instant.now().getNano(), dataBean.getAddressProofImage());

            if (null == attachmentHashValueAddressProofImage) {
                throw new FlowException("AddressProofImage could not be uploaded successfully");
            }

            attachmentHashValueIdentityProofImage =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "IdProofImage"
                            + Instant.now().getNano(), dataBean.getIdentityProofImage());

            if (null == attachmentHashValueIdentityProofImage) {
                throw new FlowException("IdProofImage could not be uploaded successfully");
            }


            List<AbstractParty> listOfListeners = new ArrayList<>();
            AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
            listOfListeners.add(listener);


            final SignedTransaction signedTx = rpcOps.startFlowDynamic(PCCCreateDataFlow.class,
                    owner,
                    dataBean,
                    attachmentHashValueAddressProofImage,
                    attachmentHashValueIdentityProofImage,
                    listOfListeners).getReturnValue().get();

            System.out.println("\nPCCDataState created with transaction id: " + signedTx.getId()
                    + " and linear id: " + signedTx.getCoreTransaction().outputsOfType(PCCDataState.class).get(0).getLinearId()
                    + " and attachmentHashValueAddressProofImage: " + attachmentHashValueAddressProofImage
                    + " and attachmentHashValueIdentityProofImage: " + attachmentHashValueIdentityProofImage);

            final String msg = String.format("%s\n",
                    signedTx.getCoreTransaction().outputsOfType(PCCDataState.class).get(0).getLinearId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    /**
     * This method downloads an attachment file from the corresponding node's vault DB
     */
    @POST
    @Path("downloadFileForId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFileForId(DownloadFileBean dataBean) throws IOException {
        String fileZip = new PoliceCommonUtils().downloadFile(dataBean.getFileURL(), dataBean.getDestinationFilePath());

        String destDir = dataBean.getDestinationFilePath();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(fileZip)));
        ZipEntry zipEntry = zis.getNextEntry();
        byte[] buffer = new byte[1024];
        while (zipEntry != null) {
            File newFile = new File(destDir + "file." + dataBean.getFileType());
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        final String msg = String.format("File downloaded to filepath: " + dataBean.getDestinationFilePath());
        return Response.status(OK).entity(msg).build();
    }

    /**
     * This method transfers the ownership of the PCC State data from one to another role
     * within the same owner node/organization
     */
    @POST
    @Path("pccTransferToAnotherRole")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccTransferToAnotherRole(PCCTransferToAnotherRoleBean dataBean) {
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            rpcOps.startFlowDynamic(PCCTransferToAnotherRoleFlow.class,
                    dataBean.getSubmittedTo(),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Application sent to next role in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method transfers the ownership of state data from one to another node/organization
     */
    @POST
    @Path("pccTransferOrganization")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccTransferOrganization(PCCTransferOrganizationBean dataBean) {
        Party newOwner = rpcOps.partiesFromName(dataBean.getNewOwner(), false).iterator().next();
        if (null == newOwner) {
            final String msg = "Invalid owner";
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener1 = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener1);
        AbstractParty listener2 = rpcOps.partiesFromName("DCRB", false).iterator().next();
        listOfListeners.add(listener2);

        try {
            rpcOps.startFlowDynamic(PCCTransferOrganizationFlow.class,
                    newOwner,
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Application assigned to " + dataBean.getNewOwner());
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method updates the physical verification flag in the state object
     */
    @POST
    @Path("pccUpdatePhysicalVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccUpdatePhysicalVerification(PCCUpdatePhysicalVerificatonBean dataBean) {
        SecureHash secureHash = null;
        try {
            secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "imagePhysicalVerification"
                    + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("imagePhysicalVerification could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("pccUpdatePhysicalVerification secureHash has some issue");
        }

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            if (null != dataBean.getFilePath() && !dataBean.getFilePath().trim().isEmpty()) {
                rpcOps.startFlowDynamic(PCCUpdatePhysicalVerificationFlow.class,
                        secureHash,
                        dataBean,
                        listOfListeners,
                        linearId).getReturnValue().get();
            } else {
                rpcOps.startFlowDynamic(PCCUpdatePhysicalVerificationFlow.class,
                        dataBean,
                        listOfListeners,
                        linearId).getReturnValue().get();
            }

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Physical verification applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method updates the criminal history in the state object
     */
    @POST
    @Path("pccUpdateCriminalHistory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccUpdateCriminalHistory(PCCUpdateCriminalHistoryBean dataBean) {
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener1 = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener1);
        AbstractParty listener2 = rpcOps.partiesFromName("DCRB", false).iterator().next();
        listOfListeners.add(listener2);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            rpcOps.startFlowDynamic(PCCUpdateCriminalHistoryFlow.class,
                    dataBean,
                    listOfListeners,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Criminal history added to ledger");
        return Response.status(CREATED).entity(msg).build();
    }

    @POST
    @Path("pccFinalizeCriminalStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccFinalizeCriminalStatus(PCCFinalizeCriminalRecordBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener); // once criminal record allocated DCRB not able to see this data anymore

        try {
            rpcOps.startFlowDynamic(PCCFinalizeCriminalRecordFlow.class,
                    new Boolean(dataBean.getIsCriminal()),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Criminal status applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method finally approves or rejects with an image and description a PCC application
     */
    @POST
    @Path("pccApproveOrReject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccApproveOrReject(PCCApproveOrRejectBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener); // once criminal record allocated DCRB not able to see this data anymore

        SecureHash secureHash = null;

        if (StringUtil.isNotBlank(dataBean.getFilePath())) {
            try {
                secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "rejectionFile"
                        + Instant.now().getNano(), dataBean.getFilePath());
                if (null == secureHash) {
                    throw new FlowException("rejectionFile could not be uploaded successfully");
                }
            } catch (Exception e) {
                System.out.println("pccApproveOrReject secureHash has some issue");
            }
        }

        try {
            if (secureHash != null) {
                rpcOps.startFlowDynamic(PCCApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        secureHash,
                        listOfListeners,
                        linearId).getReturnValue().get();
            } else {
                rpcOps.startFlowDynamic(PCCApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        listOfListeners,
                        linearId).getReturnValue().get();
            }

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Approve/Reject applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method saves the approved user's certificate to vault DB
     */
    @POST
    @Path("pccSaveCertificate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pccSaveCertificate(PCCSavePDFBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        SecureHash secureHash = null;

        try {
            secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "certificate"
                    + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("certificate could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("pccSaveCertificate secureHash has some issue");
        }

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(PCCSaveCertificateFlow.class,
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    secureHash,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Certificate saved to ledger");
        return Response.status(CREATED).entity(msg).build();
    }

    /**PCC Code Ends**/


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
     * This method creates a new PCC state in the DSBO node's vault DB
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
     * This method transfers the ownership of the PCC State data from one to another role
     * within the same owner node/organization
     */
    /*@POST
    @Path("passportTransferToAnotherRole")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportTransferToAnotherRole(PCCTransferToAnotherRoleBean dataBean) {
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            rpcOps.startFlowDynamic(PassportTransferToAnotherRoleFlow.class,
                    dataBean.getSubmittedTo(),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();

        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Application sent to next role in workflow");
        return Response.status(CREATED).entity(msg).build();
    }*/

    /**
     * This method transfers the ownership of state data from one to another node/organization
     */
    /*@POST
    @Path("passportTransferOrganization")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportTransferOrganization(PCCTransferOrganizationBean dataBean) {
        Party newOwner = rpcOps.partiesFromName(dataBean.getNewOwner(), false).iterator().next();
        if (null == newOwner) {
            final String msg = "Invalid owner";
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener1 = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener1);
        AbstractParty listener2 = rpcOps.partiesFromName("DCRB", false).iterator().next();
        listOfListeners.add(listener2);

        try {
            rpcOps.startFlowDynamic(PassportTransferOrganizationFlow.class,
                    newOwner,
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();

        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Application assigned to " + dataBean.getNewOwner());
        return Response.status(CREATED).entity(msg).build();
    }*/

    /**
     * This method updates the physical verification flag in the state object
     */
    @POST
    @Path("passportUpdatePhysicalVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportUpdatePhysicalVerification(PCCUpdatePhysicalVerificatonBean dataBean) {
        SecureHash secureHash = null;

        try {
            secureHash = new PoliceCommonUtils().createSecureHash(rpcOps, "imagePhysicalVerification"
                    + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("imagePhysicalVerification could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("imagePhysicalVerification secureHash file has some issue");
        }

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listenerDSBO = rpcOps.partiesFromName("DSBO", false).iterator().next();
        AbstractParty listenerDCRB = rpcOps.partiesFromName("DCRB", false).iterator().next();
        AbstractParty listenerFVO = rpcOps.partiesFromName("FVO", false).iterator().next();
        listOfListeners.add(listenerDSBO);
        listOfListeners.add(listenerDCRB);
        listOfListeners.add(listenerFVO);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            if (null != dataBean.getFilePath() && !dataBean.getFilePath().trim().isEmpty()) {
                rpcOps.startFlowDynamic(PassportUpdatePhysicalVerificationFlow.class,
                        secureHash,
                        dataBean,
                        listOfListeners,
                        linearId).getReturnValue().get();
            } else {
                rpcOps.startFlowDynamic(PassportUpdatePhysicalVerificationFlow.class,
                        dataBean,
                        listOfListeners,
                        linearId).getReturnValue().get();
            }

        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Physical verification applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method updates the criminal history in the state object
     */
    @POST
    @Path("passportUpdateCriminalHistory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportUpdateCriminalHistory(PassportUpdateCriminalHistoryBean dataBean) {
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listenerDSBO = rpcOps.partiesFromName("DSBO", false).iterator().next();
        AbstractParty listenerDCRB = rpcOps.partiesFromName("DCRB", false).iterator().next();
        AbstractParty listenerFVO = rpcOps.partiesFromName("FVO", false).iterator().next();
        listOfListeners.add(listenerDSBO);
        listOfListeners.add(listenerDCRB);
        listOfListeners.add(listenerFVO);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            rpcOps.startFlowDynamic(PassportUpdateCriminalHistoryFlow.class,
                    dataBean,
                    listOfListeners,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Criminal history added to ledger");
        return Response.status(CREATED).entity(msg).build();
    }

    /*@POST
    @Path("passportFinalizeCriminalStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportFinalizeCriminalStatus(PCCFinalizeCriminalRecordBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener); // once criminal record allocated DCRB not able to see this data anymore

        try {
            rpcOps.startFlowDynamic(PassportFinalizeCriminalRecordFlow.class,
                    new Boolean(dataBean.getIsCriminal()),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Criminal status applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }*/

    /**
     * This method finally approves or rejects with an image and description a PCC application
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
                    throw new FlowException("rejectionFile could not be uploaded successfully");
                }
            } catch (Exception e) {
                System.out.println("passportApproveOrReject secureHash file has some issue");
            }
        }

        try {
            if (secureHash != null) {
                rpcOps.startFlowDynamic(PassportApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        secureHash,
                        listOfListeners,
                        linearId).getReturnValue().get();
            } else {
                rpcOps.startFlowDynamic(PassportApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        listOfListeners,
                        linearId).getReturnValue().get();
            }

        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Approve/Reject applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method saves the approved user's certificate to vault DB
     */
    @POST
    @Path("passportSaveCertificate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passportSaveCertificate(PCCSavePDFBean dataBean) {
        if (!rpcOps.nodeInfo().getLegalIdentities().get(0).getName().toString().contains("DSBO")) {
            return Response.status(BAD_REQUEST).entity("Only Commissioner in DSBO node can Approve/Reject Passport Application").build();
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
        } catch (Throwable ex) {
            final String msg = ex.getMessage().split("#")[1];
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Certificate saved to ledger");
        return Response.status(CREATED).entity(msg).build();
    }


    /**PASSPORT Code to End here**/

    /**
     * NOC Code starts here
     */
    @GET
    @Path("getAllNOCDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<NOCDataState>> getAllNOCDetails() {
        return rpcOps.vaultQuery(NOCDataState.class).getStates();
    }

    @GET
    @Path("getAllNOCDetailsPageWise")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<NOCDataState>> getAllNOCDetailsPageWise(@QueryParam("pageNumber") Integer pageNumber,
                                                                              @QueryParam("maxPageSize") Integer maxPageSize) {
        PageSpecification pageSpec = new PageSpecification(pageNumber, maxPageSize);
        QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        return rpcOps.vaultQueryByWithPagingSpec(NOCDataState.class, queryCriteria, pageSpec).getStates();
    }

    @GET
    @Path("getNOCDataForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<NOCDataState>> getNOCDataForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);
        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.UNCONSUMED,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, NOCDataState.class).getStates();
    }


    @GET
    @Path("trackNOCDataForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<NOCDataState>> trackNOCDataForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);

        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.ALL,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, NOCDataState.class).getStates();
    }


    @POST
    @Path("nocCreateData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocCreateData(NOCApplicationDetailsBean dataBean) {
        SecureHash attachmentHashValueFileImage = null;
        SecureHash attachmentHashValueCertificateOfRegistration = null;
        SecureHash attachmentHashValueCertificateOfInsurance = null;
        SecureHash attachmentHashValueCertificateRCOwnerDrivingLicense = null;
        SecureHash attachmentHashValueCertificatePUC = null;
        try {
            attachmentHashValueFileImage = new PoliceCommonUtils().createSecureHash(rpcOps, "nocFile"
                    + Instant.now().getNano(), dataBean.getFileImage());
            if (null == attachmentHashValueFileImage) {
                throw new FlowException("nocFile could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println(attachmentHashValueFileImage + " has some issue");
        }

        try {
            attachmentHashValueCertificateOfRegistration = new PoliceCommonUtils().createSecureHash(rpcOps,
                    "CertificateOfRegistrationFile" + Instant.now().getNano(),
                    dataBean.getCertificateOfRegistration());

            if (null == attachmentHashValueCertificateOfRegistration) {
                throw new FlowException("CertificateOfRegistrationFile could not be uploaded successfully");
            }

            attachmentHashValueCertificateOfInsurance =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "CertificateOfInsurance"
                            + Instant.now().getNano(), dataBean.getCertificateOfInsurance());

            if (null == attachmentHashValueCertificateOfInsurance) {
                throw new FlowException("CertificateOfInsurance could not be uploaded successfully");
            }

            attachmentHashValueCertificateRCOwnerDrivingLicense =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "CertificateRCOwnerDrivingLicenseFile"
                            + Instant.now().getNano(), dataBean.getCertificateRCOwnerDrivingLicense());
            if (null == attachmentHashValueCertificateRCOwnerDrivingLicense) {
                throw new FlowException("CertificateRCOwnerDrivingLicenseFile could not be uploaded successfully");
            }

            attachmentHashValueCertificatePUC =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "CertificatePUCFile"
                            + Instant.now().getNano(), dataBean.getCertificatePUC());
            if (null == attachmentHashValueCertificatePUC) {
                throw new FlowException("CertificatePUCFile could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("nocCreateData New Attachment uploading has some issue");
        }

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener);

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(NOCCreateDataFlow.class,
                    dataBean,
                    attachmentHashValueFileImage,

                    attachmentHashValueCertificateOfRegistration,
                    attachmentHashValueCertificateOfInsurance,
                    attachmentHashValueCertificateRCOwnerDrivingLicense,
                    attachmentHashValueCertificatePUC,

                    listOfListeners).getReturnValue().get();

            System.out.println("\nNOCDataState created with transaction id: " + signedTx.getId()
                    + " and linear id: " + signedTx.getCoreTransaction().outputsOfType(NOCDataState.class).get(0)
                    .getLinearId() + " and attachmentHashValueFileImage: " + attachmentHashValueFileImage);

            final String msg = String.format("%s\n",
                    signedTx.getCoreTransaction().outputsOfType(NOCDataState.class).get(0).getLinearId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    @POST
    @Path("nocTransferToAnotherRole")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocTransferToAnotherRole(PCCTransferToAnotherRoleBean dataBean) {
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            rpcOps.startFlowDynamic(NOCTransferToAnotherRoleFlow.class,
                    dataBean.getSubmittedTo(),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Application sent to next role in workflow");
        return Response.status(CREATED).entity(msg).build();
    }


    @POST
    @Path("nocTransferOrganization")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocTransferOrganization(PCCTransferOrganizationBean dataBean) {
        Party newOwner = rpcOps.partiesFromName(dataBean.getNewOwner(), false).iterator().next();
        if (null == newOwner) {
            final String msg = "Invalid owner";
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener1 = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener1);
        AbstractParty listener2 = rpcOps.partiesFromName("DCRB", false).iterator().next();
        listOfListeners.add(listener2);

        try {
            rpcOps.startFlowDynamic(NOCTransferOrganizationFlow.class,
                    newOwner,
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Application assigned to " + dataBean.getNewOwner());
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method updates the physical verification flag in the state object
     */
    @POST
    @Path("nocUpdatePhysicalVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocUpdatePhysicalVerification(PCCUpdatePhysicalVerificatonBean dataBean) {
        SecureHash secureHash = null;
        try {
            secureHash =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "imagePhysicalVerification"
                            + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("imagePhysicalVerification could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("nocUpdatePhysicalVerification secureHash has some issue");
        }

        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            if (null != dataBean.getFilePath() && !dataBean.getFilePath().trim().isEmpty()) {
                rpcOps.startFlowDynamic(NOCUpdatePhysicalVerificationFlow.class,
                        secureHash,
                        dataBean,
                        listOfListeners,
                        linearId).getReturnValue().get();
            } else {
                rpcOps.startFlowDynamic(NOCUpdatePhysicalVerificationFlow.class,
                        dataBean,
                        listOfListeners,
                        linearId).getReturnValue().get();
            }

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Physical verification applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method updates the criminal history in the state object
     */
    @POST
    @Path("nocUpdateCriminalHistory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocUpdateCriminalHistory(PCCUpdateCriminalHistoryBean dataBean) {
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener1 = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener1);
        AbstractParty listener2 = rpcOps.partiesFromName("DCRB", false).iterator().next();
        listOfListeners.add(listener2);

        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());

        try {
            rpcOps.startFlowDynamic(NOCUpdateCriminalHistoryFlow.class,
                    dataBean,
                    listOfListeners,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Criminal history added to ledger");
        return Response.status(CREATED).entity(msg).build();
    }

    @POST
    @Path("nocFinalizeCriminalStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocFinalizeCriminalStatus(PCCFinalizeCriminalRecordBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener); // once criminal record allocated DCRB not able to see this data anymore

        try {
            rpcOps.startFlowDynamic(NOCFinalizeCriminalRecordFlow.class,
                    new Boolean(dataBean.getIsCriminal()),
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    listOfListeners,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Criminal status applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method finally approves or rejects with an image and description a NOC application
     */
    @POST
    @Path("nocApproveOrReject")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocApproveOrReject(PCCApproveOrRejectBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        List<AbstractParty> listOfListeners = new ArrayList<>();
        AbstractParty listener = rpcOps.partiesFromName("DSBO", false).iterator().next();
        listOfListeners.add(listener); // once criminal record allocated DCRB not able to see this data anymore

        SecureHash secureHash = null;
        if (StringUtil.isNotBlank(dataBean.getFilePath())) {
            try {
                secureHash =
                        new PoliceCommonUtils().createSecureHash(rpcOps, "rejectionFile"
                                + Instant.now().getNano(), dataBean.getFilePath());
                if (null == secureHash) {
                    throw new FlowException("rejectionFile could not be uploaded successfully");
                }
            } catch (Exception e) {
                System.out.println("nocApproveOrReject secureHash has some issue");
            }
        }


        try {
            if (secureHash != null) {
                rpcOps.startFlowDynamic(NOCApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        secureHash,
                        listOfListeners,
                        linearId).getReturnValue().get();
            } else {
                rpcOps.startFlowDynamic(NOCApprovalFlow.class,
                        new Boolean(dataBean.getIsApproved()),
                        dataBean.getFinalRemarks(),
                        dataBean.getUpdatedBy(),
                        dataBean.getUpdateTimeStamp(),
                        dataBean.getIpAddress(),
                        listOfListeners,
                        linearId).getReturnValue().get();
            }

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Approve/Reject applied to application in workflow");
        return Response.status(CREATED).entity(msg).build();
    }

    /**
     * This method saves the approved user's certificate to vault DB
     */
    @POST
    @Path("nocSaveCertificate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response nocSaveCertificate(PCCSavePDFBean dataBean) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(dataBean.getId());
        SecureHash secureHash = null;

        try {
            secureHash =
                    new PoliceCommonUtils().createSecureHash(rpcOps, "certificate"
                            + Instant.now().getNano(), dataBean.getFilePath());
            if (null == secureHash) {
                throw new FlowException("certificate could not be uploaded successfully");
            }
        } catch (Exception e) {
            System.out.println("nocSaveCertificate secureHash has some issue");
        }

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(NOCSaveCertificateFlow.class,
                    dataBean.getUpdatedBy(),
                    dataBean.getUpdateTimeStamp(),
                    dataBean.getIpAddress(),
                    secureHash,
                    linearId).getReturnValue().get();
        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }

        final String msg = String.format("Certificate saved to ledger");
        return Response.status(CREATED).entity(msg).build();
    }

    /**NOC Code ends here*/
}