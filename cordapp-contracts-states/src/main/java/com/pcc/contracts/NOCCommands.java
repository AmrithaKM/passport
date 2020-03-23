package com.pcc.contracts;

import net.corda.core.contracts.CommandData;

public interface NOCCommands extends CommandData {
    class NOCCreateApplication implements NOCCommands {}
    class NOCTransferRole implements NOCCommands {}
    class NOCTransferOrganization implements NOCCommands {}

    class NOCUpdateCriminalHistory implements NOCCommands {}

    class NOCUpdatePhysicalVerification implements NOCCommands {}
    class NOCFinalizeCriminalStatus implements NOCCommands {}
    class NOCApproveOrReject implements NOCCommands {}
    class NOCSaveCertificate implements NOCCommands {}
}