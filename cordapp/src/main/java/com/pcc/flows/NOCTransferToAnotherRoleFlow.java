package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.contracts.NOCCommands;
import com.pcc.contracts.NOCContract;
import com.pcc.contracts.PCCContract;
import com.pcc.states.NOCApplicationDetailsState;
import com.pcc.states.NOCDataState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
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
public class NOCTransferToAnotherRoleFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(NOCTransferToAnotherRoleFlow.class);
    private final String submittedTo;
    private final String updatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCTransferToAnotherRoleFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCTransferToAnotherRoleFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCTransferToAnotherRoleFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCTransferToAnotherRoleFlow(String submittedTo,
                                        String updatedBy,
                                        String updateTimeStamp,
                                        String ipAddress,
                                        List<AbstractParty> listOfListeners,
                                        UniqueIdentifier linearId) {
        this.submittedTo = submittedTo;
        this.updatedBy = updatedBy;
        this.updateTimeStamp = updateTimeStamp;
        this.ipAddress = ipAddress;
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

        logger.info("Submitted To : "+submittedTo.toString());
        logger.info("Updated By  : "+updatedBy.toString());
        logger.info("The updated Time Stamp is : "+updateTimeStamp.toString());
        logger.info("The IP Address is   : "+ipAddress.toString());
        logger.info("List Of Listeners are : "+listOfListeners.toString());
        logger.info("The linear Id  is: "+linearId.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria
                .LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<NOCDataState> inputStateAndRef = getServiceHub().getVaultService()
                .queryBy(NOCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        NOCDataState nocDataState = inputStateAndRef.getState().getData();
        NOCApplicationDetailsState nocApplicationDetailsState = nocDataState.getNocApplicationDetailsState();
        nocApplicationDetailsState.setUserId(updatedBy);
        nocApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        nocApplicationDetailsState.setIpAddress(ipAddress);
        nocApplicationDetailsState.setSubmittedTo(submittedTo);

        nocDataState.getNocApplicationDetailsState().setUserId(updatedBy);
        NOCDataState updatedNOCDataState = nocDataState.transferToAnotherRole(nocApplicationDetailsState,
                listOfListeners,
                "Assigned to " + submittedTo + " officer");

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedNOCDataState, NOCContract.ID)
                .addCommand(new NOCCommands.NOCTransferRole(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}