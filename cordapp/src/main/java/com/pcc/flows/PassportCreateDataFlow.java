package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.pcc.bean.PassportApplicationDetailsBean;
import com.pcc.contracts.PassportCommands;
import com.pcc.contracts.PassportContract;
import com.pcc.states.CriminalHistoryState;
import com.pcc.states.PassportApplicationDetailsState;
import com.pcc.states.PassportDataState;
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
public class PassportCreateDataFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportCreateDataFlow.class);
    private final Party ownerParty;
    private final PassportApplicationDetailsBean passportApplicationDetailsBean;
    private final List<AbstractParty> listOfListeners;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportCreateDataFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportCreateDataFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportCreateDataFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportCreateDataFlow(Party ownerParty,
                                  PassportApplicationDetailsBean passportApplicationDetailsBean,
                                  List<AbstractParty> listOfListeners) {

        this.ownerParty = ownerParty;
        this.passportApplicationDetailsBean = passportApplicationDetailsBean;
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

        logger.info("The owner Party is : "+ownerParty.toString());
        logger.info("passportApplicationDetailsBean : "+passportApplicationDetailsBean.toString());
        logger.info("list Of Listeners are : "+listOfListeners.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        PassportApplicationDetailsState passportApplicationDetailsState = createState();


        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PassportDataState passportDataState = new PassportDataState(ownerParty,
                passportApplicationDetailsState,
                listOfListeners);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(passportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportCreateApplication(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));

    }

    private PassportApplicationDetailsState createState() {
        PassportApplicationDetailsState passportApplicationDetailsState = new PassportApplicationDetailsState();
        passportApplicationDetailsState.setUid(passportApplicationDetailsBean.getUid());
        passportApplicationDetailsState.setUpdateTimeStamp(passportApplicationDetailsBean.getUpdateTimeStamp());
        passportApplicationDetailsState.setIpAddress(passportApplicationDetailsBean.getIpAddress());

        /*User's Personal Details Starts*/
        passportApplicationDetailsState.setUserId(passportApplicationDetailsBean.getUserId());
        passportApplicationDetailsState.setFirstName(passportApplicationDetailsBean.getFirstName());
        passportApplicationDetailsState.setMiddleName(passportApplicationDetailsBean.getMiddleName());
        passportApplicationDetailsState.setLastName(passportApplicationDetailsBean.getLastName());
        passportApplicationDetailsState.setEmailId(passportApplicationDetailsBean.getEmailId());
        passportApplicationDetailsState.setMobileNumber(passportApplicationDetailsBean.getMobileNumber());
        passportApplicationDetailsState.setGender(passportApplicationDetailsBean.getGender());
        passportApplicationDetailsState.setDob(passportApplicationDetailsBean.getDob());
        passportApplicationDetailsState.setParentName(passportApplicationDetailsBean.getParentName());

        /*User's Personal Details Ends*/

        /*User's Address Details Starts*/
        passportApplicationDetailsState.setVerificationAddress(passportApplicationDetailsBean.getVerificationAddress());
        passportApplicationDetailsState.setPermanentAddress(passportApplicationDetailsBean.getPermanentAddress());

        passportApplicationDetailsState.setPoliceStation(passportApplicationDetailsBean.getPoliceStation());
        /*User's Address Details Ends*/

        passportApplicationDetailsState.setFileNumber(passportApplicationDetailsBean.getFileNumber());
        passportApplicationDetailsState.setActivityDate(passportApplicationDetailsBean.getActivityDate());
        passportApplicationDetailsState.setActivityType(passportApplicationDetailsBean.getActivityType());
        passportApplicationDetailsState.setDphqIdName(passportApplicationDetailsBean.getDphqIdName());
        passportApplicationDetailsState.setPvRequestId(passportApplicationDetailsBean.getPvRequestId());
        passportApplicationDetailsState.setPvInitiationDate(passportApplicationDetailsBean.getPvInitiationDate());
        passportApplicationDetailsState.setPvRequestStatus(passportApplicationDetailsBean.getPvRequestStatus());
        passportApplicationDetailsState.setPvSequenceNumber(passportApplicationDetailsBean.getPvSequenceNumber());
        passportApplicationDetailsState.setFieldVerificationMode(passportApplicationDetailsBean.getFieldVerificationMode());

        passportApplicationDetailsState.setPlaceOfBirth(passportApplicationDetailsBean.getPlaceOfBirth());
        passportApplicationDetailsState.setSpouceName(passportApplicationDetailsBean.getSpouceName());
        passportApplicationDetailsState.setFileVerificatinMode(passportApplicationDetailsBean.getFileVerificationMode());

        passportApplicationDetailsState.setDescription(passportApplicationDetailsBean.getDescription());
        passportApplicationDetailsState.setFieldDescription(passportApplicationDetailsBean.getFieldDescription());

        passportApplicationDetailsState.setPurpose(passportApplicationDetailsBean.getPurpose());
        passportApplicationDetailsState.setSubmittedTo(passportApplicationDetailsBean.getSubmittedTo());

        List<CriminalHistoryState> listCriminalHistoryState = new ArrayList<CriminalHistoryState>();
        passportApplicationDetailsState.setListCriminalHistoryState(listCriminalHistoryState);

        return passportApplicationDetailsState;
    }
}