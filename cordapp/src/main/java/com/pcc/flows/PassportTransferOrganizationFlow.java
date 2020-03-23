package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.contracts.PCCCommands;
import com.pcc.contracts.PCCContract;
import com.pcc.contracts.PassportCommands;
import com.pcc.contracts.PassportContract;
import com.pcc.states.PassportApplicationDetailsState;
import com.pcc.states.PassportDataState;
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
public class PassportTransferOrganizationFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportTransferOrganizationFlow.class);

    private final Party newOwner;
    private final String updatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;


    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportTransferOrganizationFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportTransferOrganizationFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportTransferOrganizationFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportTransferOrganizationFlow(Party newOwner,
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

        logger.info("updatedBy : "+updatedBy.toString());
        logger.info("updateTimeStamp : "+updateTimeStamp.toString());
        logger.info("ipAddress : "+ipAddress.toString());
        logger.info("listOfListeners : "+listOfListeners.toString());
        logger.info("linearId : "+linearId.toString());

        String status = "";
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<PassportDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);

        PassportDataState passportDataState = inputStateAndRef.getState().getData();
        PassportApplicationDetailsState passportApplicationDetailsState = passportDataState.getPassportApplicationDetailsState();
        passportApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        passportApplicationDetailsState.setIpAddress(ipAddress);

        passportApplicationDetailsState.setUserId(updatedBy);
        if (newOwner.getName().toString().contains("DCRB")) {
            status = "DSBO assigned application to DCRB & Junior DSBO Officer";
            passportApplicationDetailsState.setSubmittedTo("DCRBCPO");
        } else if (newOwner.getName().toString().contains("DSBO")) {
            status = "DCRB assigned application back to DSBO";
            passportApplicationDetailsState.setSubmittedTo("DSBOCPO");
        } else {
            throw new FlowException("New Owner " + newOwner.getName().toString() + " is not a valid node");
        }

        PassportDataState updatedPassportDataState = passportDataState.transferOrganization(newOwner,
                passportApplicationDetailsState, listOfListeners, status);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedPassportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportTransferOrganization(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}