package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.bean.PCCUpdatePhysicalVerificatonBean;
import com.pcc.bean.PassportUpdatePhysicalVerificatonBean;
import com.pcc.contracts.PCCCommands;
import com.pcc.contracts.PCCContract;
import com.pcc.contracts.PassportCommands;
import com.pcc.contracts.PassportContract;
import com.pcc.states.PassportApplicationDetailsState;
import com.pcc.states.PassportDataState;
import com.pcc.states.PassportPhysicalVerificationState;
import io.netty.util.internal.StringUtil;
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
public class PassportUpdatePhysicalVerificationFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportUpdatePhysicalVerificationFlow.class);

    private final SecureHash secureHash;
    private final PassportUpdatePhysicalVerificatonBean dataBean;
    private final String partyName;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportUpdatePhysicalVerificationFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportUpdatePhysicalVerificationFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportUpdatePhysicalVerificationFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportUpdatePhysicalVerificationFlow(SecureHash secureHash,
                                                  PassportUpdatePhysicalVerificatonBean dataBean,
                                                  String partyName,
                                                  List<AbstractParty> listOfListeners,
                                                  UniqueIdentifier linearId) {
        this.secureHash = secureHash;
        this.dataBean = dataBean;
        this.partyName = partyName;
        this.listOfListeners = listOfListeners;
        this.linearId = linearId;
    }

    public PassportUpdatePhysicalVerificationFlow(PassportUpdatePhysicalVerificatonBean dataBean,
                                                  String partyName,
                                                  List<AbstractParty> listOfListeners,
                                                  UniqueIdentifier linearId) {
        this.secureHash = null;
        this.dataBean = dataBean;
        this.partyName = partyName;
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
        logger.info("dataBean : "+dataBean.toString());
        logger.info("partyName : "+partyName.toString());
        logger.info("listOfListeners : "+listOfListeners.toString());
        logger.info("linearId : "+linearId.toString());

        String statusMessage = "";
        System.out.println("PassportUpdatePhysicalVerificationFlow 1");
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null
                , ImmutableList.of(linearId.getId()));
        StateAndRef<PassportDataState> inputStateAndRef = getServiceHub().getVaultService()
                .queryBy(PassportDataState.class, queryCriteria).getStates().get(0);
        System.out.println("PassportUpdatePhysicalVerificationFlow 2");
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PassportDataState passportDataState = inputStateAndRef.getState().getData();
        PassportApplicationDetailsState passportApplicationDetailsState = passportDataState.getPassportApplicationDetailsState();
        System.out.println("PassportUpdatePhysicalVerificationFlow 3");
        passportApplicationDetailsState.setUserId(dataBean.getUpdatedBy());
        passportApplicationDetailsState.setUpdateTimeStamp(dataBean.getUpdateTimeStamp());
        passportApplicationDetailsState.setIpAddress(dataBean.getIpAddress());

        passportApplicationDetailsState.setLatitude(new Double(dataBean.getLatitude()));
        passportApplicationDetailsState.setLongitude(new Double(dataBean.getLongitude()));
        passportApplicationDetailsState.setLocationName(dataBean.getLocationName());

        System.out.println("PassportUpdatePhysicalVerificationFlow 4");
        PassportPhysicalVerificationState passportPhysicalVerificationState = new PassportPhysicalVerificationState();
        passportPhysicalVerificationState.setFieldDescription(dataBean.getFieldDescription());
        passportPhysicalVerificationState.setFlagQuestion1(new Boolean(dataBean.getFlagQuestion1()));
        passportPhysicalVerificationState.setFlagQuestion2(new Boolean(dataBean.getFlagQuestion2()));
        passportPhysicalVerificationState.setFlagQuestion3(new Boolean(dataBean.getFlagQuestion3()));
        passportPhysicalVerificationState.setFlagQuestion4(new Boolean(dataBean.getFlagQuestion4()));
        passportPhysicalVerificationState.setFlagQuestion5(new Boolean(dataBean.getFlagQuestion5()));
        passportPhysicalVerificationState.setFlagQuestion6(new Boolean(dataBean.getFlagQuestion6()));
        System.out.println("PassportUpdatePhysicalVerificationFlow 5");
        passportPhysicalVerificationState.setRemarkQuestion1(dataBean.getRemarkQuestion1());
        passportPhysicalVerificationState.setRemarkQuestion2(dataBean.getRemarkQuestion2());
        passportPhysicalVerificationState.setRemarkQuestion3(dataBean.getRemarkQuestion3());
        passportPhysicalVerificationState.setRemarkQuestion4(dataBean.getRemarkQuestion4());
        passportPhysicalVerificationState.setRemarkQuestion5(dataBean.getRemarkQuestion5());
        passportPhysicalVerificationState.setRemarkQuestion6(dataBean.getRemarkQuestion6());
        System.out.println("PassportUpdatePhysicalVerificationFlow 6");

        if (null == dataBean.getRole() || dataBean.getRole().isEmpty()) {
            System.out.println("PassportUpdatePhysicalVerificationFlow 7");
            throw new FlowException("#role is absent in request#");
        }

        if ("FVO".equalsIgnoreCase(partyName) && !StringUtil.isNullOrEmpty(dataBean.getAdharNo())) {
            System.out.println("PassportUpdatePhysicalVerificationFlow  Adhar no 7a");
            passportApplicationDetailsState.setAdharNo(dataBean.getAdharNo());
        }

        if ("FVO".equalsIgnoreCase(partyName)) {
            statusMessage = "FVO Submitted Physical Verification";
        } else {
            statusMessage = "SHO Submitted Physical Verification";
        }

        if (dataBean.getRole().equalsIgnoreCase("DSBOCPO")) {
            System.out.println("PassportUpdatePhysicalVerificationFlow 8");
            passportApplicationDetailsState.setPassportPhysicalVerificationStateDSBO(passportPhysicalVerificationState);
        } else if (dataBean.getRole().equalsIgnoreCase("FVO")) {
            System.out.println("PassportUpdatePhysicalVerificationFlow 9");
            passportApplicationDetailsState.setPassportPhysicalVerificationStateFVO(passportPhysicalVerificationState);
        } else {
            System.out.println("PassportUpdatePhysicalVerificationFlow 10");
            throw new FlowException("#Only roles DSBOCPO/FVO allowed#");
        }
        System.out.println("PassportUpdatePhysicalVerificationFlow 11");
        PassportDataState updatedPassportDataState = passportDataState.updatePhysicalVerification(passportApplicationDetailsState,
                secureHash,
                listOfListeners,
                statusMessage);

        System.out.println("PassportUpdatePhysicalVerificationFlow 12");

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedPassportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportUpdatePhysicalVerification(), getOurIdentity().getOwningKey());
        System.out.println("PassportUpdatePhysicalVerificationFlow 13");
        if (null != secureHash) {
            builder.addAttachment(secureHash);
        }
        System.out.println("PassportUpdatePhysicalVerificationFlow 14");
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}