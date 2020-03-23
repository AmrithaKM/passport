package com.pcc.contracts;

import net.corda.core.contracts.CommandData;

public interface PCCCommands extends CommandData {
    class PCCCreateApplication implements PCCCommands {}
    class PCCTransferRole implements PCCCommands {}
    class PCCTransferOrganization implements PCCCommands {}

    class PCCUpdateCriminalHistory implements PCCCommands {}

    class PCCUpdatePhysicalVerification implements PCCCommands {}
    class PCCFinalizeCriminalStatus implements PCCCommands {}
    class PCCApproveOrReject implements PCCCommands {}
    class PCCSaveCertificate implements PCCCommands {}

    /*class PassportCreateApplication implements PCCCommands {}
    class PassportDCRBReport implements PCCCommands {}
    class PassportFieldReport implements PCCCommands {}
    class PassportFinalReport implements PCCCommands {}
    class PassportApproval implements PCCCommands {}*/
}