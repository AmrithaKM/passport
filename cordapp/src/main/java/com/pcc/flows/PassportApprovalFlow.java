package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.contracts.PCCCommands;
import com.pcc.contracts.PCCContract;
import com.pcc.contracts.PassportCommands;
import com.pcc.contracts.PassportContract;
import com.pcc.states.PassportApplicationDetailsState;
import com.pcc.states.PassportDataState;
import com.pcc.utilities.AllowedRoles;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class PassportApprovalFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportApprovalFlow.class);
    private final Boolean isApproved;
    private final String finalRemarks;
    private final String updatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private String status;
    private final SecureHash secureHash;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportApprovalFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportApprovalFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportApprovalFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportApprovalFlow(Boolean isApproved,
                           String finalRemarks,
                           String updatedBy,
                           String updateTimeStamp,
                           String ipAddress,
                           SecureHash secureHash,
                           List<AbstractParty> listOfListeners,
                           UniqueIdentifier linearId) {
        this.isApproved = isApproved;
        this.finalRemarks = finalRemarks;
        this.updatedBy = updatedBy;
        this.updateTimeStamp = updateTimeStamp;
        this.ipAddress = ipAddress;
        this.secureHash = secureHash;
        this.listOfListeners = listOfListeners;
        this.linearId = linearId;
    }

    public PassportApprovalFlow(Boolean isApproved,
                           String finalRemarks,
                           String updatedBy,
                           String updateTimeStamp,
                           String ipAddress,
                           List<AbstractParty> listOfListeners,
                           UniqueIdentifier linearId) {
        this.isApproved = isApproved;
        this.finalRemarks = finalRemarks;
        this.updatedBy = updatedBy;
        this.updateTimeStamp = updateTimeStamp;
        this.ipAddress = ipAddress;
        this.secureHash = null;
        this.listOfListeners = listOfListeners;
        this.linearId = linearId;
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

        logger.info("Value of isApproved  is: "+isApproved.toString());
        logger.info("Final Remarks are  : "+finalRemarks.toString());
        logger.info("Updated By : "+updatedBy.toString());
        logger.info("The updated Time Stamp  is: "+updateTimeStamp.toString());
        logger.info("The IP Address is  : "+ipAddress.toString());
        logger.info("secure Hash value is : "+secureHash.toString());
        logger.info("list Of Listeners are : "+listOfListeners.toString());
        logger.info("The linearID is: "+linearId.toString());

        System.out.println("passportApproveOrReject 4");
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("#There is no data in this node against this lineadId#");
        }
        StateAndRef<PassportDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().get(0);
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PassportDataState passportDataState = inputStateAndRef.getState().getData();
        System.out.println("passportApproveOrReject 5: " + passportDataState.getPassportApplicationDetailsState());
        if (passportDataState.getPassportApplicationDetailsState().getPassportPhysicalVerificationStateDSBO() == null
                && passportDataState.getPassportApplicationDetailsState().getPassportPhysicalVerificationStateFVO() == null) {
            throw new FlowException("#Physical verification not complete by DSBO & FVO#");
        }

        System.out.println("passportApproveOrReject 6");

        if (passportDataState.getPassportApplicationDetailsState().getPassportPhysicalVerificationStateDSBO() == null) {
            throw new FlowException("#Physical verification not complete by DSBO#");
        }

        System.out.println("passportApproveOrReject 7");

        if (passportDataState.getPassportApplicationDetailsState().getPassportPhysicalVerificationStateFVO() == null) {
            throw new FlowException("#Physical verification not complete by FVO#");
        }
        System.out.println("passportApproveOrReject 8");
        if (passportDataState.getPassportApplicationDetailsState().getListCriminalHistoryState() == null
                || passportDataState.getPassportApplicationDetailsState().getListCriminalHistoryState().size() == 0) {
            throw new FlowException("#Criminal verification not complete by DCRB#");
        }
        System.out.println("passportApproveOrReject 9");
        PassportApplicationDetailsState passportApplicationDetailsState = passportDataState.getPassportApplicationDetailsState();
        passportApplicationDetailsState.setFinalRemarks(finalRemarks);
        passportApplicationDetailsState.setUserId(updatedBy);
        passportApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        passportApplicationDetailsState.setIpAddress(ipAddress);
        System.out.println("passportApproveOrReject 10");
        if(isApproved){
            status = "DSBO Senior Officer Approved Application";
        }
        else{
            status = "DSBO Senior Officer Rejected Application";
        }
        System.out.println("passportApproveOrReject 11");
        passportDataState = passportDataState.approval(isApproved,
                passportApplicationDetailsState,
                secureHash,
                listOfListeners,
                status);
        System.out.println("passportApproveOrReject 12");
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(passportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportApproveOrReject(), getOurIdentity().getOwningKey());
        System.out.println("passportApproveOrReject 13");
        if (null != secureHash) {
            builder.addAttachment(secureHash);
        }
        System.out.println("passportApproveOrReject 14");
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}