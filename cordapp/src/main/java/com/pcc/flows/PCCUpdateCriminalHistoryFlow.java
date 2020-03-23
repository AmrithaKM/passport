package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.bean.PCCUpdateCriminalHistoryBean;
import com.pcc.contracts.PCCCommands;
import com.pcc.contracts.PCCContract;
import com.pcc.states.CriminalHistoryState;
import com.pcc.states.PCCApplicationDetailsState;
import com.pcc.states.PCCDataState;
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

import java.util.ArrayList;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class PCCUpdateCriminalHistoryFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PCCUpdateCriminalHistoryFlow.class);

    private final PCCUpdateCriminalHistoryBean dataBean;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PCCUpdateCriminalHistoryFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PCCUpdateCriminalHistoryFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PCCUpdateCriminalHistoryFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PCCUpdateCriminalHistoryFlow(PCCUpdateCriminalHistoryBean dataBean,
                                        List<AbstractParty> listOfListeners,
                                        UniqueIdentifier linearId) {
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

        logger.info("dataBean : "+dataBean.toString());
        logger.info("listOfListeners : "+listOfListeners.toString());
        logger.info("linearId : "+linearId.toString());

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(PCCDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<PCCDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(PCCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PCCDataState pccDataState = inputStateAndRef.getState().getData();
        PCCApplicationDetailsState pccApplicationDetailsState = pccDataState.getPccApplicationDetailsState();
        pccApplicationDetailsState.setUserId(dataBean.getUpdatedBy());
        pccApplicationDetailsState.setUpdateTimeStamp(dataBean.getUpdateTimeStamp());
        pccApplicationDetailsState.setIpAddress(dataBean.getIpAddress());

        List<CriminalHistoryState> listCriminalHistoryState = new ArrayList<>();
        CriminalHistoryState criminalHistoryState = null;
        for (int i=0; i< dataBean.getListCriminalHistoryBean().size(); i++) {
            criminalHistoryState = new CriminalHistoryState();
            criminalHistoryState.setFirstName(dataBean.getListCriminalHistoryBean().get(i).getFirstName());
            criminalHistoryState.setLastName(dataBean.getListCriminalHistoryBean().get(i).getLastName());
            criminalHistoryState.setRelativeName(dataBean.getListCriminalHistoryBean().get(i).getRelativeName());

            /*criminalHistoryState.setCrimeDetails(dataBean.getListCriminalHistoryBean().get(i).getCrimeDetails());
            criminalHistoryState.setCrimeLaw(dataBean.getListCriminalHistoryBean().get(i).getCrimeLaw());
            criminalHistoryState.setCrimeSection(dataBean.getListCriminalHistoryBean().get(i).getCrimeSection());
            criminalHistoryState.setCrimeJurisdiction(dataBean.getListCriminalHistoryBean().get(i).getCrimeJurisdiction());*/
            //criminalHistoryState.setIsSelected(dataBean.getListCriminalHistoryBean().get(i).getIsSelected());
            listCriminalHistoryState.add(criminalHistoryState);
        }
        pccApplicationDetailsState.setListCriminalHistoryState(listCriminalHistoryState);
        String status = "";

        PCCDataState updatedPCCDataState = null;
        updatedPCCDataState = pccDataState.updateCriminalStatusByDCRB(
                pccApplicationDetailsState,
                listOfListeners,
                "DCRB Added Criminal History");


        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedPCCDataState, PCCContract.ID)
                .addCommand(new PCCCommands.PCCUpdateCriminalHistory(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}