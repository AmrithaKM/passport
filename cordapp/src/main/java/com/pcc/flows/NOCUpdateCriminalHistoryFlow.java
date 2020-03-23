package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.bean.PCCUpdateCriminalHistoryBean;
import com.pcc.contracts.NOCCommands;
import com.pcc.contracts.NOCContract;
import com.pcc.contracts.PCCContract;
import com.pcc.states.CriminalHistoryState;
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

import java.util.ArrayList;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class NOCUpdateCriminalHistoryFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(NOCUpdateCriminalHistoryFlow.class);

    private final PCCUpdateCriminalHistoryBean dataBean;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction NOCUpdateCriminalHistoryFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key NOCUpdateCriminalHistoryFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction NOCUpdateCriminalHistoryFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public NOCUpdateCriminalHistoryFlow(PCCUpdateCriminalHistoryBean dataBean,
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


        logger.info("dataBean  : "+dataBean.toString());
        logger.info("list Of Listeners are : "+listOfListeners.toString());
        logger.info("The linear ID is : "+linearId.toString());


        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("There is no data in this node against this lineadId");
        }
        StateAndRef<NOCDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(NOCDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        NOCDataState nocDataState = inputStateAndRef.getState().getData();
        NOCApplicationDetailsState nocApplicationDetailsState = nocDataState.getNocApplicationDetailsState();
        nocApplicationDetailsState.setUserId(dataBean.getUpdatedBy());
        nocApplicationDetailsState.setUpdateTimeStamp(dataBean.getUpdateTimeStamp());
        nocApplicationDetailsState.setIpAddress(dataBean.getIpAddress());

        List<CriminalHistoryState> listCriminalHistoryState = new ArrayList<CriminalHistoryState>();
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
        nocApplicationDetailsState.setListCriminalHistoryState(listCriminalHistoryState);

        String status = "";

        NOCDataState updatedNOCDataState = null;
        updatedNOCDataState = nocDataState.updateCriminalStatusByDCRB(
                nocApplicationDetailsState,
                listOfListeners,
                "DCRB Added Criminal History");


        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedNOCDataState, NOCContract.ID)
                .addCommand(new NOCCommands.NOCUpdateCriminalHistory(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}