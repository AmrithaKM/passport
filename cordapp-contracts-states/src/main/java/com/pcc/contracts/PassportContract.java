package com.pcc.contracts;

import com.pcc.states.PassportDataState;
import com.google.common.collect.ImmutableList;
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

public class PassportContract implements Contract {
    public static final String ID = "com.pcc.contracts.PassportContract";

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
        CommandWithParties<PassportCommands> command = requireSingleCommand(tx.getCommands(), com.pcc.contracts.PassportCommands.class);
        com.pcc.contracts.PassportCommands commandType = command.getValue();

        
        if (commandType instanceof PassportCommands.PassportCreateApplication) verifyPassportCreateData(tx, command);
        if (commandType instanceof PassportCommands.PassportTransferRole) verifyPassportTransferRole(tx, command);
        if (commandType instanceof PassportCommands.PassportTransferOrganization) verifyPassportTransferOrganization(tx, command);
        if (commandType instanceof PassportCommands.PassportUpdateCriminalHistory) verifyPassportUpdateCriminalHistory(tx, command);
        if (commandType instanceof PassportCommands.PassportUpdatePhysicalVerification)
            verifyPassportUpdatePhysicalVerification(tx, command);
        if (commandType instanceof PassportCommands.PassportFinalizeCriminalStatus) verifyPassportFinalizeCriminalStatus(tx, command);
        if (commandType instanceof PassportCommands.PassportApproveOrReject) verifyPassportApproveOrReject(tx, command);
        if (commandType instanceof PassportCommands.PassportSaveCertificate) verifyPassportSaveCertificate(tx, command);
        

    }

    
    private void verifyPassportCreateData(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("#A PassportDataState transaction should consume no input states.#",
                    tx.getInputs().isEmpty());
            require.using("#A PassportDataState transaction should only create one output state.#",
                    tx.getOutputs().size() == 1);

            final PassportDataState out = tx.outputsOfType(PassportDataState.class).get(0);
            require.using("#There must only be one signer (owner) in a PassportDataState transaction.#",
                    command.getSigners().size() == 1);
            final Party owner = out.getOwner();

            require.using("#The owner must be a signer in a PassportDataState transaction.#",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));

            require.using("#The submittedTo value " + out.getPassportApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed in list of officers/roles.#",
                    allowedRoleList.contains(out.getPassportApplicationDetailsState().getSubmittedTo()));

            require.using("#All applications must be submittedTo Commissioner.#",
                    out.getPassportApplicationDetailsState().getSubmittedTo().equalsIgnoreCase("Commissioner"));

            require.using("#The purpose value" + out.getPassportApplicationDetailsState().getPurpose()
                            + " is not in allowed list of purposes.#",
                    allowedPurposeList.contains(out.getPassportApplicationDetailsState().getPurpose()));

            return null;
        });
    }

    private void verifyPassportTransferRole(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportCommon(tx, command);
        //verifyPassportIsApproved(tx, command);
        verifyPassportNode(tx, command, "DSBO");
        requireThat(require -> {
            require.using("#Not submitted to another officer/role.#", !tx.outputsOfType(PassportDataState.class)
                    .get(0).getPassportApplicationDetailsState().getSubmittedTo().equalsIgnoreCase(tx.inputsOfType(PassportDataState.class)
                            .get(0).getPassportApplicationDetailsState().getSubmittedTo()));

            require.using("#The submittedTo value" + tx.outputsOfType(PassportDataState.class).get(0).getPassportApplicationDetailsState().getSubmittedTo()
                            + " is not present in allowed list of officers/roles.#",
                    allowedRoleList.contains(tx.outputsOfType(PassportDataState.class).get(0).getPassportApplicationDetailsState().getSubmittedTo()));

            return null;
        });
    }

    private void verifyPassportTransferOrganization(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            verifyPassportIsApproved(tx, command);
            final PassportDataState out = tx.outputsOfType(PassportDataState.class).get(0);
            final Party owner = out.getOwner();
            require.using("#There must only be one signer (owner) in a PassportDataState transaction.#", command.getSigners().size() == 1);
            require.using("#A PassportDataState transaction should consume one input state.#", tx.getInputs().size() == 1);
            require.using("#A PassportDataState transaction should only create one output state.#", tx.getOutputs().size() == 1);
            require.using("#Owner must change.#", !tx.outputsOfType(PassportDataState.class)
                    .get(0).getOwner().getName().toString().equalsIgnoreCase(tx.inputsOfType(PassportDataState.class)
                            .get(0).getOwner().getName().toString()));
            return null;
        });
    }

    private void verifyPassportUpdateCriminalHistory(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
        //verifyPassportNode(tx, command, "DCRB");
    }

    private void verifyPassportUpdatePhysicalVerification(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
        //verifyPassportNode(tx, command, "DSBO");
    }

    private void verifyPassportFinalizeCriminalStatus(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
        //verifyPassportNode(tx, command, "DSBO");
    }

    private void verifyPassportApproveOrReject(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportCommon(tx, command);
        verifyPassportIsApproved(tx, command);
        //verifyPassportNode(tx, command, "DSBO");
        requireThat(require -> {

            require.using("#Must be approved or rejected.#",
                    null != tx.outputsOfType(PassportDataState.class).get(0).getApproved());

            boolean isApproved = tx.outputsOfType(PassportDataState.class).get(0).getApproved().booleanValue();

            /*if (!isApproved) {
                require.using("#With rejection there must be an image.#",
                        tx.outputsOfType(PassportDataState.class).get(0).getRejectionAttachmentHashValue() != null);
            }*/

            String purpose = tx.outputsOfType(PassportDataState.class).get(0).getPassportApplicationDetailsState()
                    .getPurpose();

            if ("Abroad".equalsIgnoreCase(purpose)) {
                require.using("#If purpose is Abroad it must be approved by Commissioner.#",
                        tx.outputsOfType(PassportDataState.class).get(0).getPassportApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("Commissioner"));
            }

            if ("OutsideKerala".equalsIgnoreCase(purpose)) {
                require.using("#If purpose is OutsideKerala it must be approved by DSBODCP.#",
                        tx.outputsOfType(PassportDataState.class).get(0).getPassportApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("DSBODCP"));
            }

            if ("InsideKerala".equalsIgnoreCase(purpose)) {
                require.using("#If purpose is InsideKerala it must be approved by DSBOACP.#",
                        tx.outputsOfType(PassportDataState.class).get(0).getPassportApplicationDetailsState()
                                .getSubmittedTo().equalsIgnoreCase("DSBOACP"));
            }

            return null;
        });
    }

    private void verifyPassportSaveCertificate(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        verifyPassportCommon(tx, command);
        //verifyPassportNode(tx, command, "DSBO");
        requireThat(require -> {
            boolean isApproved = tx.inputsOfType(PassportDataState.class).get(0).getApproved().booleanValue();
            require.using("#The application is not yet Approved/Rejected.#",
                    null != tx.inputsOfType(PassportDataState.class).get(0).getApproved());

            //require.using("#The application is Rejected.#", isApproved);
            return null;
        });
    }

    private void verifyPassportCommon(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            final PassportDataState out = tx.outputsOfType(PassportDataState.class).get(0);
            final Party owner = out.getOwner();
            /*require.using("#The owner must be a signer in a PassportDataState transaction.#",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));*/
            require.using("#There must only be one signer (owner) in a PassportDataState transaction.#", command.getSigners().size() == 1);
            require.using("#A PassportDataState transaction should consume one input state.#", tx.getInputs().size() == 1);
            require.using("#A PassportDataState transaction should only create one output state.#", tx.getOutputs().size() == 1);
            require.using("#The submittedTo value " + out.getPassportApplicationDetailsState().getSubmittedTo()
                            + " is not in allowed list of roles/officers.#",
                    allowedRoleList.contains(out.getPassportApplicationDetailsState().getSubmittedTo()));
            return null;
        });
    }

    private void verifyPassportIsApproved(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("#The application is already Approved/Rejected.#",
                    null == tx.inputsOfType(PassportDataState.class).get(0).getApproved());
            return null;
        });
    }

    private void verifyPassportNode(LedgerTransaction tx, CommandWithParties command, String nodeName) throws IllegalArgumentException {
        /*requireThat(require -> {
            require.using("#Only " + nodeName + " can do this.#",
                    tx.outputsOfType(PassportDataState.class).get(0).getOwner().getName().toString().contains(nodeName));
            return null;
        });*/
    }
   

}