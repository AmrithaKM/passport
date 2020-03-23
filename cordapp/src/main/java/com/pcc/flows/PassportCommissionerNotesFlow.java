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
public class PassportCommissionerNotesFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportCommissionerNotesFlow.class);

    private final UniqueIdentifier linearId;
	private final String commissionerNotes;
    private final String updatedBy;
	private final String updateTimeStamp;
	private final String ipAddress;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportCommissionerNotesFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportCommissionerNotesFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportCommissionerNotesFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportCommissionerNotesFlow(UniqueIdentifier linearId,
                                         String commissionerNotes,
                                         String updatedBy,
                                         String updateTimeStamp,
                                         String ipAddress) {
        this.linearId = linearId;
		this.commissionerNotes = commissionerNotes;
		this.updatedBy = updatedBy;
		this.updateTimeStamp = updateTimeStamp;
		this.ipAddress = ipAddress;
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

        logger.info("The linearID is : "+linearId.toString());
        logger.info("commissioner Notes : "+commissionerNotes.toString());
        logger.info("Updated By : "+updatedBy.toString());
        logger.info("The updated Time Stamp : "+updateTimeStamp.toString());
        logger.info("The IP Address is  : "+ipAddress.toString());

        System.out.println("PassportCommissionerNotesFlow 1");
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("#There is no data in this node against this linearId#");
        }
        StateAndRef<PassportDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().get(0);
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PassportDataState passportDataState = inputStateAndRef.getState().getData();
        PassportApplicationDetailsState passportApplicationDetailsState = passportDataState.getPassportApplicationDetailsState();
        passportApplicationDetailsState.setUserId(updatedBy);
        passportApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        passportApplicationDetailsState.setIpAddress(ipAddress);
        passportDataState = passportDataState.updateCommissionerNotes(commissionerNotes, passportApplicationDetailsState);

        System.out.println("PassportCommissionerNotesFlow 2");
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(passportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportViewedByCommissioner(), getOurIdentity().getOwningKey());
        System.out.println("PassportCommissionerNotesFlow 3");

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}