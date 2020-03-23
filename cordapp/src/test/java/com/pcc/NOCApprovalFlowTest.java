package com.pcc;

import com.pcc.flows.NOCApprovalFlow;
import com.pcc.states.NOCDataState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionState;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;

public class NOCApprovalFlowTest {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;
    private StartedMockNode c;
    private StartedMockNode d;

    @Before
    public void setup() {
        network = new MockNetwork(ImmutableList.of("com.pcc", "com.pcc.flows"));
        a = network.createPartyNode(null);
        b = network.createPartyNode(null);
        c = network.createPartyNode(null);
        d = network.createPartyNode(null);


        network.runNetwork();


    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void transactionConstructedByFlowUsesTheCorrectNotary() throws Exception {
        NOCApprovalFlow flow = new NOCApprovalFlow(ImmutableList.of(b.getInfo().getLegalIdentities().get(0),c.getInfo().getLegalIdentities().get(0),d.getInfo().getLegalIdentities().get(0)));

        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();
        assertEquals(1, signedTransaction.getTx().getOutputStates().size());

        TransactionState output = signedTransaction.getTx().getOutputs().get(0);
        //doubted part
//        System.out.println("get notary"+output.getNotary().toString());
//        System.out.println("get notary"+network.getNotaryNodes().get(0).getInfo().getLegalIdentities().get(0));
        assertEquals(network.getNotaryNodes().get(0).getInfo().getLegalIdentities().get(0), output.getNotary());
    }

    @Test
    public void transactionConstructedByFlowHasOneTokenStateOutputWithTheCorrectAmountAndRecipient() throws Exception {
        NOCApprovalFlow flow = new NOCApprovalFlow(ImmutableList.of(b.getInfo().getLegalIdentities().get(0)));

        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();

        assertEquals(1, signedTransaction.getTx().getOutputStates().size());
        NOCDataState output = signedTransaction.getTx().outputsOfType(NOCDataState.class).get(0);
        // doubt
        assertEquals(b.getInfo().getLegalIdentities().get(0), output.getOwner());
        //assertEquals(99, output.getAmount());
    }

    @Test
    public void transactionConstructedByFlowHasOneOutputUsingTheCorrectContract() throws Exception {
        NOCApprovalFlow flow = new NOCApprovalFlow(ImmutableList.of(b.getInfo().getLegalIdentities().get(0),c.getInfo().getLegalIdentities().get(0),d.getInfo().getLegalIdentities().get(0)));
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();

        assertEquals(1, signedTransaction.getTx().getOutputStates().size());
        TransactionState output = signedTransaction.getTx().getOutputs().get(0);

        assertEquals("com.pcc.contracts.NOCContract", output.getContract());
    }

//    @Test
//    public void transactionConstructedByFlowHasOneIssueCommand() throws Exception {
//        NOCApprovalFlow flow = new NOCApprovalFlow(ImmutableList.of(b.getInfo().getLegalIdentities().get(0)));
//
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        network.runNetwork();
//        SignedTransaction signedTransaction = future.get();
//
//        assertEquals(1, signedTransaction.getTx().getCommands().size());
//        Command command = signedTransaction.getTx().getCommands().get(0);
//
//
//        assert(command.getValue() instanceof NOCApprovalFlow.);
//    }

    @Test
    public void transactionConstructedByFlowHasOneCommandWithTheIssueAsASigner() throws Exception {
        NOCApprovalFlow flow = new NOCApprovalFlow(ImmutableList.of(b.getInfo().getLegalIdentities().get(0),c.getInfo().getLegalIdentities().get(0),d.getInfo().getLegalIdentities().get(0)));
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();

        assertEquals(1, signedTransaction.getTx().getCommands().size());
        Command command = signedTransaction.getTx().getCommands().get(0);

        assertEquals(1, command.getSigners().size());
        assert(command.getSigners().contains(a.getInfo().getLegalIdentities().get(0).getOwningKey()));
    }

    @Test
    public void transactionConstructedByFlowHasNoInputsAttachmentsOrTimewindows() throws Exception {
        NOCApprovalFlow flow = new NOCApprovalFlow(ImmutableList.of(b.getInfo().getLegalIdentities().get(0),c.getInfo().getLegalIdentities().get(0),d.getInfo().getLegalIdentities().get(0)));
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction signedTransaction = future.get();

        assertEquals(0, signedTransaction.getTx().getInputs().size());
        // The single attachment is the contract attachment.
        assertEquals(1, signedTransaction.getTx().getAttachments().size());
        assertEquals(null, signedTransaction.getTx().getTimeWindow());
    }
}