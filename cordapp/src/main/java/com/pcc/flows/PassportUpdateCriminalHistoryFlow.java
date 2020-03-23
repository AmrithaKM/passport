package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.pcc.bean.CCTNSCriminalHistoryAddressBean;
import com.pcc.bean.CCTNSCriminalHistoryFIRBean;
import com.pcc.bean.PassportUpdateCriminalHistoryBean;
import com.pcc.contracts.PassportCommands;
import com.pcc.contracts.PassportContract;
import com.pcc.states.*;
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
public class PassportUpdateCriminalHistoryFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(PassportUpdateCriminalHistoryFlow.class);

    private final PassportUpdateCriminalHistoryBean dataBean;
    private final List<AbstractParty> listOfListeners;
    private final UniqueIdentifier linearId;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction PassportUpdateCriminalHistoryFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key PassportUpdateCriminalHistoryFlow.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction PassportUpdateCriminalHistoryFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public PassportUpdateCriminalHistoryFlow(PassportUpdateCriminalHistoryBean dataBean,
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

        System.out.println("PassportUpdateCriminalHistoryFlow 1");
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        if (getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().size() == 0) {
            throw new FlowException("#There is no data in this node against this lineadId#");
        }
        System.out.println("PassportUpdateCriminalHistoryFlow 2");
        StateAndRef<PassportDataState> inputStateAndRef = getServiceHub().getVaultService().queryBy(PassportDataState.class, queryCriteria).getStates().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PassportDataState passportDataState = inputStateAndRef.getState().getData();
        PassportApplicationDetailsState passportApplicationDetailsState = passportDataState.getPassportApplicationDetailsState();
        passportApplicationDetailsState.setUserId(dataBean.getUpdatedBy());
        passportApplicationDetailsState.setUpdateTimeStamp(dataBean.getUpdateTimeStamp());
        passportApplicationDetailsState.setIpAddress(dataBean.getIpAddress());
        System.out.println("PassportUpdateCriminalHistoryFlow 3");
        List<CriminalHistoryState> listCriminalHistoryState = new ArrayList<CriminalHistoryState>();
        CriminalHistoryState criminalHistoryState = null;
        System.out.println("PassportUpdateCriminalHistoryFlow 4");
        for (int i=0; i< dataBean.getListCriminalHistoryBean().size(); i++) {
            System.out.println("PassportUpdateCriminalHistoryFlow 5" + i);
            criminalHistoryState = new CriminalHistoryState();
            criminalHistoryState.setFirstName(dataBean.getListCriminalHistoryBean().get(i).getFirstName());
            criminalHistoryState.setMiddleName(dataBean.getListCriminalHistoryBean().get(i).getMiddleName());
            criminalHistoryState.setLastName(dataBean.getListCriminalHistoryBean().get(i).getLastName());
            criminalHistoryState.setRelativeName(dataBean.getListCriminalHistoryBean().get(i).getRelativeName());

            List<CCTNSCriminalHistoryAddressBean> listCriminalHistoryAddressBean
                    = dataBean.getListCriminalHistoryBean().get(i).getListCriminalHistoryAddressBean();
            List<CriminalHistoryAddressState> listCriminalHistoryAddressState = new ArrayList<>();
            for (int j = 0; j < listCriminalHistoryAddressBean.size(); j++) {
                System.out.println("PassportUpdateCriminalHistoryFlow 6" + j);
                CriminalHistoryAddressState criminalHistoryAddressState = new CriminalHistoryAddressState();
                criminalHistoryAddressState.setAddressType(listCriminalHistoryAddressBean.get(j).getAddressType());
                criminalHistoryAddressState.setHouseNo(listCriminalHistoryAddressBean.get(j).getHouseNo());
                criminalHistoryAddressState.setStreetName(listCriminalHistoryAddressBean.get(j).getStreetName());
                criminalHistoryAddressState.setColonyLocalArea(listCriminalHistoryAddressBean.get(j).getColonyLocalArea());
                criminalHistoryAddressState.setVillageTownCity(listCriminalHistoryAddressBean.get(j).getVillageTownCity());
                criminalHistoryAddressState.setTehsilBlockMandal(listCriminalHistoryAddressBean.get(j).getTehsilBlockMandal());
                criminalHistoryAddressState.setCountry(listCriminalHistoryAddressBean.get(j).getCountry());
                criminalHistoryAddressState.setState(listCriminalHistoryAddressBean.get(j).getState());
                criminalHistoryAddressState.setDistrict(listCriminalHistoryAddressBean.get(j).getDistrict());
                criminalHistoryAddressState.setPinCode(listCriminalHistoryAddressBean.get(j).getPinCode());
                criminalHistoryAddressState.setPoliceStation(listCriminalHistoryAddressBean.get(j).getPoliceStation());
                criminalHistoryAddressState.setIsCurrent(listCriminalHistoryAddressBean.get(j).getIsCurrent());
                listCriminalHistoryAddressState.add(criminalHistoryAddressState);
            }
            criminalHistoryState.setListCriminalHistoryAddressState(listCriminalHistoryAddressState);
            System.out.println("PassportUpdateCriminalHistoryFlow 7");
            criminalHistoryState.setFir_reg_num(dataBean.getListCriminalHistoryBean().get(i).getFir_reg_num());
            criminalHistoryState.setFir_district_cd(dataBean.getListCriminalHistoryBean().get(i).getFir_district_cd());
            criminalHistoryState.setFir_ps_cd(dataBean.getListCriminalHistoryBean().get(i).getFir_ps_cd());
            criminalHistoryState.setFirDisplay(dataBean.getListCriminalHistoryBean().get(i).getFirDisplay());
            criminalHistoryState.setAccused_srno(dataBean.getListCriminalHistoryBean().get(i).getAccused_srno());
            criminalHistoryState.setAddress_srno(dataBean.getListCriminalHistoryBean().get(i).getAddress_srno());
            System.out.println("PassportUpdateCriminalHistoryFlow 8");
            List<CCTNSCriminalHistoryFIRBean> listCriminalHistoryFIRBean
                    = dataBean.getListCriminalHistoryBean().get(i).getListCriminalHistoryFIRBean();
            List<CriminalHistoryFIRState> listCriminalHistoryFIRState = new ArrayList<>();
            for (int k = 0; k < listCriminalHistoryFIRBean.size(); k++) {
                CriminalHistoryFIRState criminalHistoryFIRState = new CriminalHistoryFIRState();
                criminalHistoryFIRState.setCrimeActName(listCriminalHistoryFIRBean.get(k).getCrimeActName());
                criminalHistoryFIRState.setCrimeSection(listCriminalHistoryFIRBean.get(k).getCrimeSection());
                listCriminalHistoryFIRState.add(criminalHistoryFIRState);
            }
            System.out.println("PassportUpdateCriminalHistoryFlow 9");
            criminalHistoryState.setListCriminalHistoryFIRState(listCriminalHistoryFIRState);
            criminalHistoryState.setIsSelected(dataBean.getListCriminalHistoryBean().get(i).getIsSelected());
            listCriminalHistoryState.add(criminalHistoryState);
        }
        System.out.println("PassportUpdateCriminalHistoryFlow 10");
        passportApplicationDetailsState.setListCriminalHistoryState(listCriminalHistoryState);

        String status = "";

        PassportDataState updatedPassportDataState = null;
        updatedPassportDataState = passportDataState.updateCriminalStatusByDCRB(
                passportApplicationDetailsState,
                listOfListeners,
                "DCRB Added Criminal History");
        System.out.println("PassportUpdateCriminalHistoryFlow 11");

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addInputState(inputStateAndRef)
                .addOutputState(updatedPassportDataState, PassportContract.ID)
                .addCommand(new PassportCommands.PassportUpdateCriminalHistory(), getOurIdentity().getOwningKey());
        System.out.println("PassportUpdateCriminalHistoryFlow 12");
        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.pcc.flows.VerifySignAndFinaliseFlow(builder));
    }
}