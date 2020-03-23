package com.pcc.contracts;

import com.pcc.states.PCCDataState;
import com.google.common.collect.ImmutableList;
import com.pcc.states.PassportDataState;
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

public class PCCContract implements Contract {
    public static final String ID = "com.pcc.contracts.PCCContract";

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
        CommandWithParties<PCCCommands> command = requireSingleCommand(tx.getCommands(), com.pcc.contracts.PCCCommands.class);
        com.pcc.contracts.PCCCommands commandType = command.getValue();

        //pcc starts
        if (commandType instanceof PCCCommands.PCCCreateApplication) verifyPCCCreateData(tx, command);
        if (commandType instanceof PCCCommands.PCCTransferRole) verifyPCCTransferRole(tx, command);
        if (commandType instanceof PCCCommands.PCCTransferOrganization) verifyPCCTransferOrganization(tx, command);
        if (commandType instanceof PCCCommands.PCCUpdateCriminalHistory) verifyPCCUpdateCriminalHistory(tx, command);
        if (commandType instanceof PCCCommands.PCCUpdatePhysicalVerification)
            verifyPCCUpdatePhysicalVerification(tx, command);
        if (commandType instanceof PCCCommands.PCCFinalizeCriminalStatus) verifyPCCFinalizeCriminalStatus(tx, command);
        if (commandType instanceof PCCCommands.PCCApproveOrReject) verifyPCCApproveOrReject(tx, command);
        if (commandType instanceof PCCCommands.PCCSaveCertificate) verifyPCCSaveCertificate(tx, command);
        //pcc ends

        //passport starts
        /*if (commandType instanceof PCCCommands.PassportCreateApplication) verifyPassportCreateData(tx, command);
        if (commandType instanceof PCCCommands.PassportDCRBReport) verifyPassportDCRBReport(tx, command);
        if (commandType instanceof PCCCommands.PassportFieldReport) verifyPassportFieldReport(tx, command);
        if (commandType instanceof PCCCommands.PassportFinalReport) verifyPassportFinalReport(tx, command);
        if (commandType instanceof PCCCommands.PassportApproval) verifyPassportApproval(tx, command);*/
        //passport ends
    }

    //PCC starts
    private void verifyPCCCreateData(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A PCCDataState transaction should consume no input states.",
                    tx.getInputs().isEmpty());
            require.using("A PCCDataState transaction should only create one output state.",
                    tx.getOutputs().size() == 1);

            final PCCDataState out = tx.outputsOfType(PCCDataState.class).get(0);
            require.using("There must only be one signer (owner) in a PCCDataState transaction.",
                    command.getSigners().size() == 1);
            final Party owner = out.getOwner();

            require.using("The owner must be a signer in a PCCDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));

            require.using("The submittedTo value" + out.getPccApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of officers/roles.",
                    allowedRoleList.contains(out.getPccApplicationDetailsState().getSubmittedTo()));

            require.using("All applications must be submittedTo Commissioner",
                    out.getPccApplicationDetailsState().getSubmittedTo().equalsIgnoreCase("Commissioner"));

            require.using("The purpose value" + out.getPccApplicationDetailsState().getPurpose()
                            + " is not in allowed list of purposes.",
                    allowedPurposeList.contains(out.getPccApplicationDetailsState().getPurpose()));
            require.using("The address proof type" + out.getPccApplicationDetailsState().getAddressProofType()
                            + " is not in allowed list of allowed address proofs.",
                    allowedAddressProofList.contains(out.getPccApplicationDetailsState().getAddressProofType()));
            require.using("The id proof type " + out.getPccApplicationDetailsState().getIdentityProofType()
                            + "is not in allowed list of id proofs.",
                    allowedIdProofList.contains(out.getPccApplicationDetailsState().getIdentityProofType()));

            return null;
        });
    }

    private void verifyPCCTransferRole(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPCCCommon(tx, command);
        verifyPCCIsApproved(tx, command);
        verifyPCCNode(tx, command, "DSBO");
        requireThat(require -> {
            require.using("submittedTo must change.", !tx.outputsOfType(PCCDataState.class)
                    .get(0).getPccApplicationDetailsState().getSubmittedTo().equalsIgnoreCase(tx.inputsOfType(PCCDataState.class)
                            .get(0).getPccApplicationDetailsState().getSubmittedTo()));

            require.using("The submittedTo value" + tx.outputsOfType(PCCDataState.class).get(0).getPccApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of officers/roles.",
                    allowedRoleList.contains(tx.outputsOfType(PCCDataState.class).get(0).getPccApplicationDetailsState().getSubmittedTo()));

            return null;
        });
    }

    private void verifyPCCTransferOrganization(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            verifyPCCIsApproved(tx, command);
            final PCCDataState out = tx.outputsOfType(PCCDataState.class).get(0);
            final Party owner = out.getOwner();
            require.using("There must only be one signer (owner) in a PCCDataState transaction.", command.getSigners().size() == 1);
            require.using("A PCCDataState transaction should consume one input state.", tx.getInputs().size() == 1);
            require.using("A PCCDataState transaction should only create one output state.", tx.getOutputs().size() == 1);
            require.using("Owner must change.", !tx.outputsOfType(PCCDataState.class)
                    .get(0).getOwner().getName().toString().equalsIgnoreCase(tx.inputsOfType(PCCDataState.class)
                            .get(0).getOwner().getName().toString()));
            return null;
        });
    }

    private void verifyPCCUpdateCriminalHistory(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPCCCommon(tx, command);
        verifyPCCIsApproved(tx, command);
        verifyPCCNode(tx, command, "DCRB");
    }

    private void verifyPCCUpdatePhysicalVerification(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPCCCommon(tx, command);
        verifyPCCIsApproved(tx, command);
        verifyPCCNode(tx, command, "DSBO");
    }

    private void verifyPCCFinalizeCriminalStatus(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPCCCommon(tx, command);
        verifyPCCIsApproved(tx, command);
        verifyPCCNode(tx, command, "DSBO");
    }

    private void verifyPCCApproveOrReject(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPCCCommon(tx, command);
        verifyPCCIsApproved(tx, command);
        verifyPCCNode(tx, command, "DSBO");
        requireThat(require -> {

            require.using("Must be approved or rejected.",
                    null != tx.outputsOfType(PCCDataState.class).get(0).getApproved());

            boolean isApproved = tx.outputsOfType(PCCDataState.class).get(0).getApproved().booleanValue();

            if (!isApproved) {
                require.using("With rejection there must be an image.",
                        tx.outputsOfType(PCCDataState.class).get(0).getRejectionAttachmentHashValue() != null);
            }

            String purpose = tx.outputsOfType(PCCDataState.class).get(0).getPccApplicationDetailsState()
                    .getPurpose();

            if ("Abroad".equalsIgnoreCase(purpose)) {
                require.using("If purpose is Abroad, it must be approved by Commissioner.",
                        tx.outputsOfType(PCCDataState.class).get(0).getPccApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("Commissioner"));
            }

            if ("OutsideKerala".equalsIgnoreCase(purpose)) {
                require.using("If purpose is OutsideKerala, it must be approved by DSBODCP.",
                        tx.outputsOfType(PCCDataState.class).get(0).getPccApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("DSBODCP"));
            }

            if ("InsideKerala".equalsIgnoreCase(purpose)) {
                require.using("If purpose is InsideKerala, it must be approved by DSBOACP.",
                        tx.outputsOfType(PCCDataState.class).get(0).getPccApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("DSBOACP"));
            }

            return null;
        });
    }

    private void verifyPCCSaveCertificate(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPCCCommon(tx, command);
        verifyPCCNode(tx, command, "DSBO");
        requireThat(require -> {
            boolean isApproved = tx.inputsOfType(PCCDataState.class).get(0).getApproved().booleanValue();
            require.using("The application is not yet Approved/Rejected.",
                    null != tx.inputsOfType(PCCDataState.class).get(0).getApproved());

            require.using("The application is Rejected.", isApproved);
            return null;
        });
    }

    private void verifyPCCCommon(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            final PCCDataState out = tx.outputsOfType(PCCDataState.class).get(0);
            final Party owner = out.getOwner();
            require.using("The owner must be a signer in a PCCDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));
            require.using("There must only be one signer (owner) in a PCCDataState transaction.", command.getSigners().size() == 1);
            require.using("A PCCDataState transaction should consume one input state.", tx.getInputs().size() == 1);
            require.using("A PCCDataState transaction should only create one output state.", tx.getOutputs().size() == 1);
            require.using("The submittedTo value" + out.getPccApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of roles/officers.",
                    allowedRoleList.contains(out.getPccApplicationDetailsState().getSubmittedTo()));
            return null;
        });
    }

    private void verifyPCCIsApproved(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("The application is already Approved/Rejected.",
                    null == tx.inputsOfType(PCCDataState.class).get(0).getApproved());
            return null;
        });
    }

    private void verifyPCCNode(LedgerTransaction tx, CommandWithParties command, String nodeName) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("Only " + nodeName + " can do this.",
                    tx.outputsOfType(PCCDataState.class).get(0).getOwner().getName().toString().contains(nodeName));
            return null;
        });
    }
    //PCC ends


    /*//Passport starts
    private void verifyPassportCreateData(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("Only DSBO can do this.",
                    tx.outputsOfType(PassportDataState.class).get(0).getOwner().getName().toString().contains("DSBO"));

            require.using("A PassportDataState transaction should consume no input states.",
                    tx.getInputs().isEmpty());

            require.using("A PassportDataState transaction should only create one output state.",
                    tx.getOutputs().size() == 1);

            final PassportDataState out = tx.outputsOfType(PassportDataState.class).get(0);
            require.using("There must only be one signer (hospital) in a PassportDataState transaction.",
                    command.getSigners().size() == 1);
            final Party owner = out.getOwner();
            require.using("The owner must be a signer in a PassportDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));
            return null;
        });
    }

    //Passport starts
    private void verifyPassportDCRBReport(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportNode(tx, command, "DSBO");
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
    }

    private void verifyPassportFieldReport(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportNode(tx, command, "DSBO");
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
    }


    private void verifyPassportFinalReport(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportNode(tx, command, "DSBO");
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
    }

    private void verifyPassportApproval(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportNode(tx, command, "DSBO");
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
    }

    private void verifyPassportIsApproved(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("The application is already Approved/Rejected.",
                    null == tx.inputsOfType(PassportDataState.class).get(0).getApproved());
            return null;
        });
    }

    private void verifyPassportCommon(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A PassportDataState transaction should consume one input states.",
                    tx.getInputs().size() == 1);
            require.using("A PassportDataState transaction should only create one output state.",
                    tx.getOutputs().size() == 1);

            final PassportDataState out = tx.outputsOfType(PassportDataState.class).get(0);
            require.using("There must only be one signer (hospital) in a PassportDataState transaction.",
                    command.getSigners().size() == 1);
            final Party owner = out.getOwner();

            require.using("The owner must be a signer in a PassportDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));
            return null;
        });
    }

    private void verifyPassportNode(LedgerTransaction tx, CommandWithParties command, String nodeName) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("Only " + nodeName + " can do this.",
                    tx.inputsOfType(PassportDataState.class).get(0).getOwner().getName().toString().contains(nodeName));
            return null;
        });
    }
    //Passport ends*/

}