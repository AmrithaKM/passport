package com.pcc.contracts;

import com.google.common.collect.ImmutableList;
import com.pcc.states.NOCDataState;
import com.pcc.utilities.AllowedAddressProofs;
import com.pcc.utilities.AllowedIdProofs;
import com.pcc.utilities.AllowedPurposes;
import com.pcc.utilities.AllowedRoles;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.util.ArrayList;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class NOCContract implements Contract {
    public static final String ID = "com.pcc.contracts.NOCContract";

    private AllowedRoles allowedRoles = null;
    private ArrayList<String> allowedRoleList = null;

    private AllowedIdProofs allowedIdProofs = null;
    private ArrayList<String> allowedIdProofList = null;

    private AllowedAddressProofs allowedAddressProofs = null;
    private ArrayList<String> allowedAddressProofList = null;

    private AllowedPurposes allowedPurposes = null;
    private ArrayList<String> allowedPurposeList = null;

    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        allowedRoles = new AllowedRoles();
        allowedRoleList = allowedRoles.enumFields();

        allowedIdProofs = new AllowedIdProofs();
        allowedIdProofList = allowedIdProofs.enumFields();

        allowedAddressProofs = new AllowedAddressProofs();
        allowedAddressProofList = allowedAddressProofs.enumFields();

        allowedPurposes = new AllowedPurposes();
        allowedPurposeList = allowedPurposes.enumFields();

        verifyAll(tx);
    }

    private void verifyAll(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<NOCCommands> command = requireSingleCommand(tx.getCommands(), NOCCommands.class);
        NOCCommands commandType = command.getValue();

        //noc starts
        if (commandType instanceof NOCCommands.NOCCreateApplication) verifyNOCCreateData(tx, command);
        if (commandType instanceof NOCCommands.NOCTransferRole) verifyNOCTransferRole(tx, command);
        if (commandType instanceof NOCCommands.NOCTransferOrganization) verifyNOCTransferOrganization(tx, command);
        if (commandType instanceof NOCCommands.NOCUpdateCriminalHistory) verifyNOCUpdateCriminalHistory(tx, command);
        if (commandType instanceof NOCCommands.NOCUpdatePhysicalVerification)
            verifyNOCUpdatePhysicalVerification(tx, command);
        if (commandType instanceof NOCCommands.NOCFinalizeCriminalStatus) verifyNOCFinalizeCriminalStatus(tx, command);
        if (commandType instanceof NOCCommands.NOCApproveOrReject) verifyNOCApproveOrReject(tx, command);
        if (commandType instanceof NOCCommands.NOCSaveCertificate) verifyNOCSaveCertificate(tx, command);
        //noc ends
    }

    //NOC starts
    private void verifyNOCCreateData(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A NOCDataState transaction should consume no input states.",
                    tx.getInputs().isEmpty());
            require.using("A NOCDataState transaction should only create one output state.",
                    tx.getOutputs().size() == 1);

            final NOCDataState out = tx.outputsOfType(NOCDataState.class).get(0);
            require.using("There must only be one signer (owner) in a NOCDataState transaction.",
                    command.getSigners().size() == 1);
            final Party owner = out.getOwner();

            require.using("The create NOCDataState request must come from DSBO node.",
                    owner.getName().toString().contains("DSBO"));

            require.using("The owner must be a signer in a NOCDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));

            require.using("The submittedTo value" + out.getNocApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of officers/roles.",
                    allowedRoleList.contains(out.getNocApplicationDetailsState().getSubmittedTo()));

            require.using("All applications must be submittedTo Commissioner",
                    out.getNocApplicationDetailsState().getSubmittedTo().equalsIgnoreCase("Commissioner"));

            require.using("The purpose value" + out.getNocApplicationDetailsState().getPurpose()
                            + " is not in allowed list of purposes.",
                    allowedPurposeList.contains(out.getNocApplicationDetailsState().getPurpose()));

            return null;
        });
    }

    private void verifyNOCTransferRole(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyNOCCommon(tx, command);
        verifyNOCIsApproved(tx, command);
        verifyNOCNode(tx, command, "DSBO");

        requireThat(require -> {
            require.using("submittedTo must change.", !tx.outputsOfType(NOCDataState.class)
                    .get(0).getNocApplicationDetailsState().getSubmittedTo().equalsIgnoreCase(tx.inputsOfType(NOCDataState.class)
                            .get(0).getNocApplicationDetailsState().getSubmittedTo()));

            require.using("The submittedTo value" + tx.outputsOfType(NOCDataState.class).get(0).getNocApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of officers/roles.",
                    allowedRoleList.contains(tx.outputsOfType(NOCDataState.class).get(0).getNocApplicationDetailsState().getSubmittedTo()));
            return null;
        });
    }

    private void verifyNOCTransferOrganization(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            verifyNOCIsApproved(tx, command);
            final NOCDataState out = tx.outputsOfType(NOCDataState.class).get(0);
            final Party owner = out.getOwner();
            require.using("There must only be one signer (owner) in a NOCDataState transaction.", command.getSigners().size() == 1);
            require.using("A NOCDataState transaction should consume one input state.", tx.getInputs().size() == 1);
            require.using("A NOCDataState transaction should only create one output state.", tx.getOutputs().size() == 1);
            require.using("Owner must change.", !tx.outputsOfType(NOCDataState.class)
                    .get(0).getOwner().getName().toString().equalsIgnoreCase(tx.inputsOfType(NOCDataState.class)
                            .get(0).getOwner().getName().toString()));
            return null;
        });
    }

    private void verifyNOCUpdateCriminalHistory(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyNOCCommon(tx, command);
        verifyNOCIsApproved(tx, command);
        verifyNOCNode(tx, command, "DCRB");
    }

    private void verifyNOCUpdatePhysicalVerification(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyNOCCommon(tx, command);
        verifyNOCIsApproved(tx, command);
        verifyNOCNode(tx, command, "DSBO");
    }

    private void verifyNOCFinalizeCriminalStatus(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyNOCCommon(tx, command);
        verifyNOCIsApproved(tx, command);
        verifyNOCNode(tx, command, "DSBO");
    }

    private void verifyNOCApproveOrReject(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyNOCCommon(tx, command);
        verifyNOCIsApproved(tx, command);
        verifyNOCNode(tx, command, "DSBO");
        requireThat(require -> {

            require.using("Must be approved or rejected.",
                    null != tx.outputsOfType(NOCDataState.class).get(0).getApproved());

            boolean isApproved = tx.outputsOfType(NOCDataState.class).get(0).getApproved().booleanValue();

            if (!isApproved) {
                require.using("With rejection there must be an image.",
                        tx.outputsOfType(NOCDataState.class).get(0).getRejectionAttachmentHashValue() != null);
            }

            String purpose = tx.outputsOfType(NOCDataState.class).get(0).getNocApplicationDetailsState()
                    .getPurpose();

            if ("Abroad".equalsIgnoreCase(purpose)) {
                require.using("If purpose is Abroad, it must be approved by Commissioner.",
                        tx.outputsOfType(NOCDataState.class).get(0).getNocApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("Commissioner"));
            }

            if ("OutsideKerala".equalsIgnoreCase(purpose)) {
                require.using("If purpose is OutsideKerala, it must be approved by DSBODCP.",
                        tx.outputsOfType(NOCDataState.class).get(0).getNocApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("DSBODCP"));
            }

            if ("InsideKerala".equalsIgnoreCase(purpose)) {
                require.using("If purpose is InsideKerala, it must be approved by DSBOACP.",
                        tx.outputsOfType(NOCDataState.class).get(0).getNocApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("DSBOACP"));
            }

            return null;
        });
    }

    private void verifyNOCSaveCertificate(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyNOCCommon(tx, command);
        verifyNOCNode(tx, command, "DSBO");
        requireThat(require -> {
            boolean isApproved = tx.inputsOfType(NOCDataState.class).get(0).getApproved().booleanValue();
            require.using("The application is not yet Approved/Rejected.",
                    null != tx.inputsOfType(NOCDataState.class).get(0).getApproved());

            require.using("The application is Rejected.", isApproved);
            return null;
        });
    }

    private void verifyNOCCommon(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            final NOCDataState out = tx.outputsOfType(NOCDataState.class).get(0);
            final Party owner = out.getOwner();
            require.using("The owner must be a signer in a NOCDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));
            require.using("There must only be one signer (owner) in a NOCDataState transaction.", command.getSigners().size() == 1);
            require.using("A NOCDataState transaction should consume one input state.", tx.getInputs().size() == 1);
            require.using("A NOCDataState transaction should only create one output state.", tx.getOutputs().size() == 1);
            require.using("The submittedTo value" + out.getNocApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of roles/officers.",
                    allowedRoleList.contains(out.getNocApplicationDetailsState().getSubmittedTo()));
            return null;
        });
    }

    private void verifyNOCIsApproved(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("The application is already Approved/Rejected.",
                    null == tx.inputsOfType(NOCDataState.class).get(0).getApproved());
            return null;
        });
    }

    private void verifyNOCNode(LedgerTransaction tx, CommandWithParties command, String nodeName) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("Only " + nodeName + " can do this.",
                    tx.outputsOfType(NOCDataState.class).get(0).getOwner().getName().toString().contains(nodeName));
            return null;
        });
    }
    //NOC ends

}