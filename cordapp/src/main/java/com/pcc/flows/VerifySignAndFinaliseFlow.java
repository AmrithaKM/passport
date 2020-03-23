package com.pcc.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifySignAndFinaliseFlow extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(VerifySignAndFinaliseFlow.class);

    private final TransactionBuilder transactionBuilder;

    public VerifySignAndFinaliseFlow(TransactionBuilder transactionBuilder) {
        this.transactionBuilder = transactionBuilder;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        logger.info("transactionBuilder : "+transactionBuilder.toString());

        transactionBuilder.verify(getServiceHub());
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        return subFlow(new FinalityFlow(signedTransaction));
    }
}
