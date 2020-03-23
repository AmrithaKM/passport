package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.bean.PCCUpdatePhysicalVerificatonBean;
import com.pcc.contracts.PCCCommands;
import com.pcc.contracts.PCCContract;
import com.pcc.states.PCCApplicationDetailsState;
import com.pcc.states.PCCDataState;
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
public class PCCUpdatePhysicalVerificationFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PCCUpdatePhysicalVerificationFlow.class);

    private final SecureHash secureHash;
    private final PCCUpdatePhysicalVerificatonBean dataBean;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PCCUpdatePhysicalVerificationFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PCCUpdatePhysicalVerificationFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PCCUpdatePhysicalVerificationFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PCCUpdatePhysicalVerificationFlow(SecureHash secureHash,
                                             PCCUpdatePhysicalVerificatonBean dataBean,
                                             List<AbstractParty> listOfListeners,
                                             UniqueIdentifier linearId) {
        this.secureHash = secureHash;
        this.dataBean = dataBean;
        this.listOfListeners = listOfListeners;
        this.linearId = linearId;
    }

    public PCCUpdatePhysicalVerificationFlow(PCCUpdatePhysicalVerificatonBean dataBean,
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



        logger.info("secureHash : "+secureHash.toString());
        logger.info("dataBean  : "+dataBean.toString());
        logger.info("listOfListeners : "+listOfListeners.toString());
        logger.info("secureHash : "+linearId.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null
                , ImmutableList.of(linearId.getId()));
        StateAndRef<PCCDataState> inputStateAndRef = getServiceHub().getVaultService()
                .queryBy(PCCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PCCDataState pccDataState = inputStateAndRef.getState().getData();
        PCCApplicationDetailsState pccApplicationDetailsState = pccDataState.getPccApplicationDetailsState();

        if (!pccApplicationDetailsState.getIdentityProofId().equalsIgnoreCase(dataBean.getIdentificationNumber())) {
            throw new FlowException("The identification number does not match");
        }

        pccApplicationDetailsState.setUserId(dataBean.getUpdatedBy());
        pccApplicationDetailsState.setUpdateTimeStamp(dataBean.getUpdateTimeStamp());
        pccApplicationDetailsState.setIpAddress(dataBean.getIpAddress());
        pccApplicationDetailsState.setFieldDescription(dataBean.getFieldDescription());

        pccApplicationDetailsState.setFlagQuestion1(new Boolean(dataBean.getFlagQuestion1()));
        pccApplicationDetailsState.setFlagQuestion2(new Boolean(dataBean.getFlagQuestion2()));
        pccApplicationDetailsState.setFlagQuestion3(new Boolean(dataBean.getFlagQuestion3()));
        pccApplicationDetailsState.setFlagQuestion4(new Boolean(dataBean.getFlagQuestion4()));
        pccApplicationDetailsState.setFlagQuestion5(new Boolean(dataBean.getFlagQuestion5()));
        pccApplicationDetailsState.setFlagQuestion6(new Boolean(dataBean.getFlagQuestion6()));

        PCCDataState updatedPCCDataState = pccDataState.updatePhysicalVerification(pccApplicationDetailsState,
                secureHash,
                listOfListeners,
                "DSBO Junior Officer Submitted Physical Verification");


        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedPCCDataState, PCCContract.ID)
                .addCommand(new PCCCommands.PCCUpdatePhysicalVerification(), getOurIdentity().getOwningKey());

        if (null != secureHash) {
            builder.addAttachment(secureHash);
        }

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}