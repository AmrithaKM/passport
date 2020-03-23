package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.bean.PCCUpdatePhysicalVerificatonBean;
import com.pcc.contracts.NOCCommands;
import com.pcc.contracts.NOCContract;
import com.pcc.contracts.PCCContract;
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
public class NOCUpdatePhysicalVerificationFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(NOCUpdatePhysicalVerificationFlow.class);

    private final SecureHash secureHash;
    private final PCCUpdatePhysicalVerificatonBean dataBean;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCUpdatePhysicalVerificationFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCUpdatePhysicalVerificationFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCUpdatePhysicalVerificationFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCUpdatePhysicalVerificationFlow(SecureHash secureHash,
                                             PCCUpdatePhysicalVerificatonBean dataBean,
                                             List<AbstractParty> listOfListeners,
                                             UniqueIdentifier linearId) {
        this.secureHash = secureHash;
        this.dataBean = dataBean;
        this.listOfListeners = listOfListeners;
        this.linearId = linearId;
    }

    public NOCUpdatePhysicalVerificationFlow(String newUpdatedBy,
                                             PCCUpdatePhysicalVerificatonBean dataBean,
                                             List<AbstractParty> listOfListeners,
                                             UniqueIdentifier linearId) {
        this.secureHash = null;
        this.dataBean = dataBean;
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

        logger.info("secure Hash value is : "+secureHash.toString());
        logger.info("dataBean  : "+dataBean.toString());
        logger.info("list Of Listeners are : "+listOfListeners.toString());
        logger.info("The linear ID is : "+linearId.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null
                , ImmutableList.of(linearId.getId()));
        StateAndRef<NOCDataState> inputStateAndRef = getServiceHub().getVaultService()
                .queryBy(NOCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        NOCDataState nocDataState = inputStateAndRef.getState().getData();
        NOCApplicationDetailsState nocApplicationDetailsState = nocDataState.getNocApplicationDetailsState();

        if (!nocApplicationDetailsState.getIdentityProofId().equalsIgnoreCase(dataBean.getIdentificationNumber())) {
            throw new FlowException("The identification number does not match");
        }

        nocApplicationDetailsState.setUserId(dataBean.getUpdatedBy());
        nocApplicationDetailsState.setUpdateTimeStamp(dataBean.getUpdateTimeStamp());
        nocApplicationDetailsState.setIpAddress(dataBean.getIpAddress());

        nocApplicationDetailsState.setFlagQuestion1(new Boolean(dataBean.getFlagQuestion1()));
        nocApplicationDetailsState.setFlagQuestion2(new Boolean(dataBean.getFlagQuestion2()));
        nocApplicationDetailsState.setFlagQuestion3(new Boolean(dataBean.getFlagQuestion3()));
        nocApplicationDetailsState.setFlagQuestion4(new Boolean(dataBean.getFlagQuestion4()));
        nocApplicationDetailsState.setFlagQuestion5(new Boolean(dataBean.getFlagQuestion5()));
        nocApplicationDetailsState.setFlagQuestion6(new Boolean(dataBean.getFlagQuestion6()));

        NOCDataState updatedNOCDataState = nocDataState.updatePhysicalVerification(nocApplicationDetailsState,
                secureHash,
                listOfListeners,
                "DSBO Junior Officer Submitted Physical Verification");


        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedNOCDataState, NOCContract.ID)
                .addCommand(new NOCCommands.NOCUpdatePhysicalVerification(), getOurIdentity().getOwningKey());

        if (null != secureHash) {
            builder.addAttachment(secureHash);
        }

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}