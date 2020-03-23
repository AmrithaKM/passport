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
public class NOCTransferOrganizationFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(NOCTransferOrganizationFlow.class);

    private final Party newOwner;
    private final String updatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;


    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCTransferOrganizationFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCTransferOrganizationFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCTransferOrganizationFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCTransferOrganizationFlow(Party newOwner,
                                       String updatedBy,
                                       String updateTimeStamp,
                                       String ipAddress,
                                       List<AbstractParty> listOfListeners,
                                       UniqueIdentifier linearId) {
        this.newOwner = newOwner;
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

        logger.info("new Owner is : "+newOwner.toString());
        logger.info("updated By  : "+updatedBy.toString());
        logger.info("The updated Time Stamp is : "+updateTimeStamp.toString());
        logger.info("The IP Address is  : "+ipAddress.toString());
        logger.info("list Of Listeners are : "+listOfListeners.toString());
        logger.info("The linear Id is : "+linearId.toString());


        String status = "";
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<NOCDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);

        NOCDataState nocDataState = inputStateAndRef.getState().getData();
        NOCApplicationDetailsState nocApplicationDetailsState = nocDataState.getNocApplicationDetailsState();
        nocApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        nocApplicationDetailsState.setIpAddress(ipAddress);

        nocApplicationDetailsState.setUserId(updatedBy);
        if (newOwner.getName().toString().contains("DCRB")) {
            status = "DSBO assigned application to DCRB & Junior DSBO Officer";
            nocApplicationDetailsState.setSubmittedTo("DCRBCPO");
        } else if (newOwner.getName().toString().contains("DSBO")) {
            status = "DSBO assigned application to DCRB & Junior DSBO Officer";
            nocApplicationDetailsState.setSubmittedTo("DCRBCPO");
        } else {
            throw new FlowException("New Owner " + newOwner.getName().toString() + " is not a valid node");
        }

        NOCDataState updatedNOCDataState = nocDataState.transferOrganization(newOwner,
                nocApplicationDetailsState, listOfListeners, status);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedNOCDataState, NOCContract.ID)
                .addCommand(new NOCCommands.NOCTransferOrganization(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}