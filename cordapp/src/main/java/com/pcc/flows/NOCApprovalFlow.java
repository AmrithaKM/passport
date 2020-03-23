package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.contracts.NOCCommands;
import com.pcc.contracts.NOCContract;
import com.pcc.states.NOCApplicationDetailsState;
import com.pcc.states.NOCDataState;
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
public class NOCApprovalFlow extends FlowLogic<SignedTransaction> {
    private static final Logger Logger = LoggerFactory.getLogger(NOCApprovalFlow.class);

    private final Boolean isApproved;
    private final String finalRemarks;
    private final String updatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private final SecureHash secureHash;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;
    private String status;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCApprovalFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCApprovalFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCApprovalFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCApprovalFlow(Boolean isApproved,
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

    public NOCApprovalFlow( List<AbstractParty> listOfListeners) {
        this.isApproved = null;
        this.finalRemarks = null;
        this.updatedBy = null;
        this.updateTimeStamp = null;
        this.ipAddress = null;
        this.secureHash = null;
        this.listOfListeners = listOfListeners;
        this.linearId = null;
    }
//    public NOCApprovalFlow() {
//        this.isApproved = null;
//        this.finalRemarks = null;
//        this.updatedBy = null;
//        this.updateTimeStamp = null;
//        this.ipAddress = null;
//        this.secureHash = null;
//        this.listOfListeners = null;
//        this.linearId = null;
//    }


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

        Logger.info("Value of isApproved is   : "+isApproved.toString());
        Logger.info("Final Remarks are  : "+finalRemarks.toString());
        Logger.info("Updated By : "+updatedBy.toString());
        Logger.info("The Updated Time Stamp : "+updateTimeStamp.toString());
        Logger.info("IP Address is  : "+ipAddress.toString());
        Logger.info("Secure Hash  value is: "+secureHash.toString());
        Logger.info("List Of Listeners are : "+listOfListeners.toString());
        Logger.info("The Linear Id is: "+linearId.toString());


        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<NOCDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        NOCDataState nocDataState = inputStateAndRef.getState().getData();
        NOCApplicationDetailsState nocApplicationDetailsState = nocDataState.getNocApplicationDetailsState();
        nocApplicationDetailsState.setFinalRemarks(finalRemarks);
        nocApplicationDetailsState.setUserId(updatedBy);
        nocApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        nocApplicationDetailsState.setIpAddress(ipAddress);
        if(isApproved){
            status = "DSBO Senior Officer Approved Application";
        }
        else{
            status = "DSBO Senior Officer Rejected Application";
        }

        nocDataState = nocDataState.approvalByACP(isApproved,
                nocApplicationDetailsState,
                secureHash,
                listOfListeners,
                status);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(nocDataState, NOCContract.ID)
                .addCommand(new NOCCommands.NOCApproveOrReject(), getOurIdentity().getOwningKey());

        if (null != secureHash) {
            builder.addAttachment(secureHash);
        }

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}