package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.pcc.bean.PCCApplicationDetailsBean;
import com.pcc.contracts.PCCCommands;
import com.pcc.contracts.PCCContract;
import com.pcc.states.CriminalHistoryState;
import com.pcc.states.PCCApplicationDetailsState;
import com.pcc.states.PCCDataState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class PCCCreateDataFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PCCCreateDataFlow.class);

    private final Party ownerParty;
    private final PCCApplicationDetailsBean pccApplicationDetailsBean;
    private final SecureHash attachmentHashValueAddressProofImage;
    private final SecureHash attachmentHashValueIdentityProofImage;
    private final List<AbstractParty> listOfListeners;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PCCCreateDataFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PCCCreateDataFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PCCCreateDataFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PCCCreateDataFlow(Party ownerParty,
                             PCCApplicationDetailsBean pccApplicationDetailsBean,
                             SecureHash attachmentHashValueAddressProofImage,
                             SecureHash attachmentHashValueIdentityProofImage,
                             List<AbstractParty> listOfListeners) {

        this.ownerParty = ownerParty;
        this.pccApplicationDetailsBean = pccApplicationDetailsBean;
        this.attachmentHashValueAddressProofImage = attachmentHashValueAddressProofImage;
        this.attachmentHashValueIdentityProofImage = attachmentHashValueIdentityProofImage;
        this.listOfListeners = listOfListeners;
    }

    // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
    // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
    // function.
    private final ProgressTracker progressTracker = new ProgressTracker(
            GENERATING_TRANSACTION,
            SIGNING_TRANSACTION,
            FINALISING_TRANSACTION
    );

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        logger.info("ownerParty : "+ownerParty.toString());
        logger.info("pccApplicationDetailsBean  : "+pccApplicationDetailsBean.toString());
        logger.info("attachmentHashValueAddressProofImage : "+attachmentHashValueAddressProofImage.toString());
        logger.info("attachmentHashValueIdentityProofImage : "+attachmentHashValueIdentityProofImage.toString());
        logger.info("listOfListeners  : "+listOfListeners.toString());


        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        PCCApplicationDetailsState pccApplicationDetailsState = createState();


        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PCCDataState pccDataState = new PCCDataState(ownerParty,
                pccApplicationDetailsState,
                attachmentHashValueAddressProofImage,
                attachmentHashValueIdentityProofImage,
                listOfListeners);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(pccDataState, PCCContract.ID)
                .addAttachment(attachmentHashValueAddressProofImage)
                .addAttachment(attachmentHashValueIdentityProofImage)
                .addCommand(new PCCCommands.PCCCreateApplication(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));

    }

    private PCCApplicationDetailsState createState() {
        PCCApplicationDetailsState pccApplicationDetailsState = new PCCApplicationDetailsState();
        pccApplicationDetailsState.setUid(pccApplicationDetailsBean.getUid());
        pccApplicationDetailsState.setUpdateTimeStamp(pccApplicationDetailsBean.getUpdateTimeStamp());
        pccApplicationDetailsState.setIpAddress(pccApplicationDetailsBean.getIpAddress());

        /*User's Personal Details Starts*/

        pccApplicationDetailsState.setUserId(pccApplicationDetailsBean.getUserId());
        pccApplicationDetailsState.setFirstName(pccApplicationDetailsBean.getFirstName());
        pccApplicationDetailsState.setMiddleName(pccApplicationDetailsBean.getMiddleName());
        pccApplicationDetailsState.setLastName(pccApplicationDetailsBean.getLastName());
        pccApplicationDetailsState.setParentName(pccApplicationDetailsBean.getParentName());
        pccApplicationDetailsState.setNationality(pccApplicationDetailsBean.getNationality());
        pccApplicationDetailsState.setEmailId(pccApplicationDetailsBean.getEmailId());
        pccApplicationDetailsState.setRelationType(pccApplicationDetailsBean.getRelationType());
        pccApplicationDetailsState.setRelativeName(pccApplicationDetailsBean.getRelativeName());
        pccApplicationDetailsState.setMobileNumber1(pccApplicationDetailsBean.getMobileNumber1());
        pccApplicationDetailsState.setMobileNumber2(pccApplicationDetailsBean.getMobileNumber2());
        pccApplicationDetailsState.setLandlineNumber1(pccApplicationDetailsBean.getLandlineNumber1());
        pccApplicationDetailsState.setLandlineNumber2(pccApplicationDetailsBean.getLandlineNumber2());
        pccApplicationDetailsState.setLandlineNumber3(pccApplicationDetailsBean.getLandlineNumber3());
        pccApplicationDetailsState.setGender(pccApplicationDetailsBean.getGender());
        pccApplicationDetailsState.setDob(pccApplicationDetailsBean.getDob());

        pccApplicationDetailsState.setIdentityProofType(pccApplicationDetailsBean.getIdentityProofType());
        pccApplicationDetailsState.setIdentityProofId(pccApplicationDetailsBean.getIdentityProofId());
        /*User's Personal Details Ends*/

        /*User's Address Details Starts*/
        //Present
        pccApplicationDetailsState.setPresentHouseNo(pccApplicationDetailsBean.getPresentHouseNo());
        pccApplicationDetailsState.setPresentStreetName(pccApplicationDetailsBean.getPresentStreetName());
        pccApplicationDetailsState.setPresentColonyLocalArea(pccApplicationDetailsBean.getPresentColonyLocalArea());
        pccApplicationDetailsState.setPresentVillageTownCity(pccApplicationDetailsBean.getPresentVillageTownCity());
        pccApplicationDetailsState.setPresentTehsilBlockMandal(pccApplicationDetailsBean.getPresentTehsilBlockMandal());
        pccApplicationDetailsState.setPresentCountry(pccApplicationDetailsBean.getPresentCountry());
        pccApplicationDetailsState.setPresentState(pccApplicationDetailsBean.getPresentState());
        pccApplicationDetailsState.setPresentDistrict(pccApplicationDetailsBean.getPresentDistrict());
        pccApplicationDetailsState.setPresentPoliceStation(pccApplicationDetailsBean.getPresentPoliceStation());
        pccApplicationDetailsState.setPresentPinCode(pccApplicationDetailsBean.getPresentPinCode());

        //Permanent
        pccApplicationDetailsState.setPermanentHouseNo(pccApplicationDetailsBean.getPermanentHouseNo());
        pccApplicationDetailsState.setPermanentStreetName(pccApplicationDetailsBean.getPermanentStreetName());
        pccApplicationDetailsState.setPermanentColonyLocalArea(pccApplicationDetailsBean.getPermanentColonyLocalArea());
        pccApplicationDetailsState.setPermanentVillageTownCity(pccApplicationDetailsBean.getPermanentVillageTownCity());
        pccApplicationDetailsState.setPermanentTehsilBlockMandal(pccApplicationDetailsBean.getPermanentTehsilBlockMandal());
        pccApplicationDetailsState.setPermanentCountry(pccApplicationDetailsBean.getPermanentCountry());
        pccApplicationDetailsState.setPermanentState(pccApplicationDetailsBean.getPermanentState());
        pccApplicationDetailsState.setPermanentDistrict(pccApplicationDetailsBean.getPermanentDistrict());
        pccApplicationDetailsState.setPermanentPoliceStation(pccApplicationDetailsBean.getPermanentPoliceStation());
        pccApplicationDetailsState.setPermanentPinCode(pccApplicationDetailsBean.getPermanentPinCode());

        pccApplicationDetailsState.setAddressProofType(pccApplicationDetailsBean.getAddressProofType());
        pccApplicationDetailsState.setAddressProofId(pccApplicationDetailsBean.getAddressProofId());
        /*User's Address Details Ends*/

        pccApplicationDetailsState.setTypeOfPCC(pccApplicationDetailsBean.getTypeOfPCC());
        pccApplicationDetailsState.setDescription(pccApplicationDetailsBean.getDescription());
        pccApplicationDetailsState.setFieldDescription(pccApplicationDetailsBean.getFieldDescription());

        pccApplicationDetailsState.setPurpose(pccApplicationDetailsBean.getPurpose());
        pccApplicationDetailsState.setSubmittedTo(pccApplicationDetailsBean.getSubmittedTo());
        pccApplicationDetailsState.setTypeOfPCC(pccApplicationDetailsBean.getTypeOfPCC());

        pccApplicationDetailsState.setPreviousPCCNumber(pccApplicationDetailsBean.getPreviousPCCNumber());
        pccApplicationDetailsState.setPreviousPCCDateTaken(pccApplicationDetailsBean.getPreviousPCCDateTaken());
        pccApplicationDetailsState.setPreviousPCCPurpose(pccApplicationDetailsBean.getPreviousPCCPurpose());

        List<?> listCriminalHistoryState = new ArrayList<>();
        pccApplicationDetailsState.setListCriminalHistoryState(listCriminalHistoryState);

        return pccApplicationDetailsState;
    }
}