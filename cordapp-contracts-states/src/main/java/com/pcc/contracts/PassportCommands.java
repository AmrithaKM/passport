package com.pcc.contracts;

import net.corda.core.contracts.CommandData;

public interface PassportCommands extends CommandData {
    class PassportCreateApplication implements PassportCommands {}
    class PassportTransferRole implements PassportCommands {}
    class PassportTransferOrganization implements PassportCommands {}

    class PassportUpdateCriminalHistory implements PassportCommands {}

    class PassportUpdatePhysicalVerification implements PassportCommands {}
    class PassportFinalizeCriminalStatus implements PassportCommands {}
    class PassportViewedByCommissioner implements PassportCommands {}
    class PassportApproveOrReject implements PassportCommands {}
    class PassportSaveCertificate implements PassportCommands {}
}