package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PassportCommissionerNotesBean implements Serializable {
    private String id;
    private String commissionerNotes;
    private String updatedBy;
    private String updateTimeStamp;
    private String ipAddress;

    public String getId() {
        return id;
    }

    public String getCommissionerNotes() { return commissionerNotes; }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
