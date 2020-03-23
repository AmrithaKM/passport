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
public class PassportFinalizeCriminalRecordFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportFinalizeCriminalRecordFlow.class);

    private final Boolean isCriminal;
    private final List<AbstractParty> listOfListeners;
    private final String newUpdatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportFinalizeCriminalRecordFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportFinalizeCriminalRecordFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportFinalizeCriminalRecordFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportFinalizeCriminalRecordFlow(Boolean isCriminal,
                                         String newUpdatedBy,
                                         String updateTimeStamp,
                                         String ipAddress,
                                         List<AbstractParty> listOfListeners,
                                         UniqueIdentifier linearId) {
        this.isCriminal = isCriminal;
        this.listOfListeners = listOfListeners;
        this.newUpdatedBy = newUpdatedBy;
        this.updateTimeStamp = updateTimeStamp;
        this.ipAddress = ipAddress;
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

        logger.info("Value of isCriminal is: "+isCriminal.toString());
        logger.info("list Of Listeners are : "+listOfListeners.toString());
        logger.info("New Updated By : "+newUpdatedBy.toString());

        logger.info("The updated Time Stamp is : "+updateTimeStamp.toString());
        logger.info("The IP Address is : "+ipAddress.toString());
        logger.info("The linearId is : "+linearId.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<PassportDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PassportDataState passportDataState = inputStateAndRef.getState().getData();
        PassportApplicationDetailsState passportApplicationDetailsState = passportDataState.getPassportApplicationDetailsState();
        passportApplicationDetailsState.setUserId(newUpdatedBy);
        passportApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        passportApplicationDetailsState.setIpAddress(ipAddress);
        String status = "";

        PassportDataState updatedPassportDataState = null;
        updatedPassportDataState = passportDataState.updateCriminalStatusByDSBO(getOurIdentity(),
                passportApplicationDetailsState,
                listOfListeners,
                isCriminal, "DSBO Assigned Criminal Status");


        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedPassportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportFinalizeCriminalStatus(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}