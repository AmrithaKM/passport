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
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@InitiatingFlow
@StartableByRPC
public class NOCSaveCertificateFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(NOCSaveCertificateFlow.class);

    private final String updatedBy;
    private final String updateTimeStamp;
    private final String ipAddress;
    private final SecureHash secureHash;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCSaveCertificateFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCSaveCertificateFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCSaveCertificateFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCSaveCertificateFlow(String updatedBy,
                        String updateTimeStamp,
                        String ipAddress,
                        SecureHash secureHash,
                        UniqueIdentifier linearId) {
        this.updatedBy = updatedBy;
        this.updateTimeStamp = updateTimeStamp;
        this.ipAddress = ipAddress;
        this.secureHash = secureHash;
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



        logger.info("Updated By  : "+updatedBy.toString());
        logger.info(" The updated Time Stamp  is: "+updateTimeStamp.toString());
        logger.info("The IP Address is : "+ipAddress.toString());
        logger.info("The secure Hash is : "+secureHash.toString());
        logger.info("The linearId : "+linearId.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<NOCDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        NOCDataState nocDataState = inputStateAndRef.getState().getData();
        NOCApplicationDetailsState nocApplicationDetailsState = nocDataState.getNocApplicationDetailsState();
        nocApplicationDetailsState.setUserId(updatedBy);
        nocApplicationDetailsState.setUpdateTimeStamp(updateTimeStamp);
        nocApplicationDetailsState.setIpAddress(ipAddress);

        nocDataState = nocDataState.saveCertificate(secureHash);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(nocDataState, NOCContract.ID)
                .addCommand(new NOCCommands.NOCSaveCertificate(), getOurIdentity().getOwningKey());

        if (null != secureHash) {
            builder.addAttachment(secureHash);
        }

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}