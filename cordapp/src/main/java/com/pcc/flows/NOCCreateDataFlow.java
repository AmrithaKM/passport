package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.pcc.bean.NOCApplicationDetailsBean;
import com.pcc.contracts.NOCCommands;
import com.pcc.contracts.NOCContract;
import com.pcc.states.CriminalHistoryState;
import com.pcc.states.NOCApplicationDetailsState;
import com.pcc.states.NOCDataState;
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
public class NOCCreateDataFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(NOCCreateDataFlow.class);
    private final NOCApplicationDetailsBean nocApplicationDetailsBean;
    private final SecureHash attachmentHashValueFileImage;

    private final SecureHash attachmentHashValueCertificateOfRegistration;
    private final SecureHash attachmentHashValueCertificateOfInsurance;
    private final SecureHash attachmentHashValueCertificateRCOwnerDrivingLicense;
    private final SecureHash attachmentHashValueCertificatePUC;
    private final List<AbstractParty> listOfListeners;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCCreateDataFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCCreateDataFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCCreateDataFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCCreateDataFlow(NOCApplicationDetailsBean nocApplicationDetailsBean,
                             SecureHash attachmentHashValueFileImage,

                             SecureHash attachmentHashValueCertificateOfRegistration,
                             SecureHash attachmentHashValueCertificateOfInsurance,
                             SecureHash attachmentHashValueCertificateRCOwnerDrivingLicense,
                             SecureHash attachmentHashValueCertificatePUC,

                             List<AbstractParty> listOfListeners) {

        this.nocApplicationDetailsBean = nocApplicationDetailsBean;
        this.attachmentHashValueFileImage = attachmentHashValueFileImage;

        this.attachmentHashValueCertificateOfRegistration = attachmentHashValueCertificateOfRegistration;
        this.attachmentHashValueCertificateOfInsurance = attachmentHashValueCertificateOfInsurance;
        this.attachmentHashValueCertificateRCOwnerDrivingLicense = attachmentHashValueCertificateRCOwnerDrivingLicense;
        this.attachmentHashValueCertificatePUC = attachmentHashValueCertificatePUC;

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

        logger.info("noc Application Details Bean : "+nocApplicationDetailsBean.toString());
        logger.info("Attachment Hash Value File Image  : "+attachmentHashValueFileImage.toString());
        logger.info("Attachment Hash Value Certificate Of Registration : "+attachmentHashValueCertificateOfRegistration.toString());
        logger.info("Attachment Hash Value Certificate Of Insurance : "+attachmentHashValueCertificateOfInsurance.toString());
        logger.info("Attachment Hash Value Certificate RCOwner DrivingLicense  : "+attachmentHashValueCertificateRCOwnerDrivingLicense.toString());
        logger.info("Attachment Hash Value CertificatePUC : "+attachmentHashValueCertificatePUC.toString());


        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        NOCApplicationDetailsState nocApplicationDetailsState = createState();

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        NOCDataState nocDataState = new NOCDataState(getOurIdentity(),
                nocApplicationDetailsState,
                attachmentHashValueFileImage,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                listOfListeners);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(nocDataState, NOCContract.ID)
                .addAttachment(attachmentHashValueFileImage)

                .addAttachment(attachmentHashValueCertificateOfRegistration)
                .addAttachment(attachmentHashValueCertificateOfInsurance)
                .addAttachment(attachmentHashValueCertificateRCOwnerDrivingLicense)
                .addAttachment(attachmentHashValueCertificatePUC)

                .addCommand(new NOCCommands.NOCCreateApplication(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));

    }

    private NOCApplicationDetailsState createState() {
        NOCApplicationDetailsState nocApplicationDetailsState = new NOCApplicationDetailsState();
        nocApplicationDetailsState.setUid(nocApplicationDetailsBean.getUid());
        nocApplicationDetailsState.setUpdateTimeStamp(nocApplicationDetailsBean.getUpdateTimeStamp());
        nocApplicationDetailsState.setIpAddress(nocApplicationDetailsBean.getIpAddress());

        /*User's Personal Details Starts*/

        nocApplicationDetailsState.setUserId(nocApplicationDetailsBean.getUserId());
        nocApplicationDetailsState.setFirstName(nocApplicationDetailsBean.getFirstName());
        nocApplicationDetailsState.setMiddleName(nocApplicationDetailsBean.getMiddleName());
        nocApplicationDetailsState.setLastName(nocApplicationDetailsBean.getLastName());
        nocApplicationDetailsState.setNationality(nocApplicationDetailsBean.getNationality());
        nocApplicationDetailsState.setEmailId(nocApplicationDetailsBean.getEmailId());
        nocApplicationDetailsState.setRelationType(nocApplicationDetailsBean.getRelationType());
        nocApplicationDetailsState.setRelativeName(nocApplicationDetailsBean.getRelativeName());
        nocApplicationDetailsState.setMobileNumber1(nocApplicationDetailsBean.getMobileNumber1());
        nocApplicationDetailsState.setMobileNumber2(nocApplicationDetailsBean.getMobileNumber2());
        nocApplicationDetailsState.setLandlineNumber1(nocApplicationDetailsBean.getLandlineNumber1());
        nocApplicationDetailsState.setLandlineNumber2(nocApplicationDetailsBean.getLandlineNumber2());
        nocApplicationDetailsState.setLandlineNumber3(nocApplicationDetailsBean.getLandlineNumber3());
        nocApplicationDetailsState.setGender(nocApplicationDetailsBean.getGender());
        nocApplicationDetailsState.setDob(nocApplicationDetailsBean.getDob());
        nocApplicationDetailsState.setIdentityProofId(nocApplicationDetailsBean.getIdentityProofId());
        nocApplicationDetailsState.setIdentityProofType(nocApplicationDetailsBean.getIdentityProofType());

        /*User's Personal Details Ends*/

        /*User's Address Details Starts*/
        //Present
        nocApplicationDetailsState.setPresentHouseNo(nocApplicationDetailsBean.getPresentHouseNo());
        nocApplicationDetailsState.setPresentStreetName(nocApplicationDetailsBean.getPresentStreetName());
        nocApplicationDetailsState.setPresentColonyLocalArea(nocApplicationDetailsBean.getPresentColonyLocalArea());
        nocApplicationDetailsState.setPresentVillageTownCity(nocApplicationDetailsBean.getPresentVillageTownCity());
        nocApplicationDetailsState.setPresentTehsilBlockMandal(nocApplicationDetailsBean.getPresentTehsilBlockMandal());
        nocApplicationDetailsState.setPresentCountry(nocApplicationDetailsBean.getPresentCountry());
        nocApplicationDetailsState.setPresentState(nocApplicationDetailsBean.getPresentState());
        nocApplicationDetailsState.setPresentDistrict(nocApplicationDetailsBean.getPresentDistrict());
        nocApplicationDetailsState.setPresentPoliceStation(nocApplicationDetailsBean.getPresentPoliceStation());
        nocApplicationDetailsState.setPresentPinCode(nocApplicationDetailsBean.getPresentPinCode());

        //Permanent
        nocApplicationDetailsState.setPermanentHouseNo(nocApplicationDetailsBean.getPermanentHouseNo());
        nocApplicationDetailsState.setPermanentStreetName(nocApplicationDetailsBean.getPermanentStreetName());
        nocApplicationDetailsState.setPermanentColonyLocalArea(nocApplicationDetailsBean.getPermanentColonyLocalArea());
        nocApplicationDetailsState.setPermanentVillageTownCity(nocApplicationDetailsBean.getPermanentVillageTownCity());
        nocApplicationDetailsState.setPermanentTehsilBlockMandal(nocApplicationDetailsBean.getPermanentTehsilBlockMandal());
        nocApplicationDetailsState.setPermanentCountry(nocApplicationDetailsBean.getPermanentCountry());
        nocApplicationDetailsState.setPermanentState(nocApplicationDetailsBean.getPermanentState());
        nocApplicationDetailsState.setPermanentDistrict(nocApplicationDetailsBean.getPermanentDistrict());
        nocApplicationDetailsState.setPermanentPoliceStation(nocApplicationDetailsBean.getPermanentPoliceStation());
        nocApplicationDetailsState.setPermanentPinCode(nocApplicationDetailsBean.getPermanentPinCode());

        /*User's Address Details Ends*/

        nocApplicationDetailsState.setFileType(nocApplicationDetailsBean.getFileType());
        nocApplicationDetailsState.setFileSubType(nocApplicationDetailsBean.getFileSubType());
        nocApplicationDetailsState.setFileId(nocApplicationDetailsBean.getFileId());

        nocApplicationDetailsState.setTypeOfNOC(nocApplicationDetailsBean.getTypeOfNOC());
        nocApplicationDetailsState.setDescription(nocApplicationDetailsBean.getDescription());

        nocApplicationDetailsState.setPurpose(nocApplicationDetailsBean.getPurpose());
        nocApplicationDetailsState.setSubmittedTo(nocApplicationDetailsBean.getSubmittedTo());

        nocApplicationDetailsState.setTypeOfVehicle(nocApplicationDetailsBean.getTypeOfVehicle());
        nocApplicationDetailsState.setRegistrationNumber(nocApplicationDetailsBean.getRegistrationNumber());
        nocApplicationDetailsState.setMake(nocApplicationDetailsBean.getMake());
        nocApplicationDetailsState.setModel(nocApplicationDetailsBean.getModel());
        nocApplicationDetailsState.setEngineNumber(nocApplicationDetailsBean.getEngineNumber());
        nocApplicationDetailsState.setChasisNumber(nocApplicationDetailsBean.getChasisNumber());

        nocApplicationDetailsState.setRegisteredInAuthority(nocApplicationDetailsBean.getRegisteredInAuthority());
        nocApplicationDetailsState.setPeriodInState(nocApplicationDetailsBean.getPeriodInState());
        nocApplicationDetailsState.setMotorTaxPaidUpto(nocApplicationDetailsBean.getMotorTaxPaidUpto());

        nocApplicationDetailsState.setAnyTaxPending(nocApplicationDetailsBean.getAnyTaxPending());
        nocApplicationDetailsState.setVehicleInvolvedInTheftCase(nocApplicationDetailsBean.getVehicleInvolvedInTheftCase());
        nocApplicationDetailsState.setActionUnderMotorVehicleAct(nocApplicationDetailsBean.getActionUnderMotorVehicleAct());
        nocApplicationDetailsState.setInvolvedTransportProhibitedGoods(nocApplicationDetailsBean.getInvolvedTransportProhibitedGoods());

        List<CriminalHistoryState> listCriminalHistoryState = new ArrayList<CriminalHistoryState>();
        nocApplicationDetailsState.setListCriminalHistoryState(listCriminalHistoryState);

        return nocApplicationDetailsState;
    }
}