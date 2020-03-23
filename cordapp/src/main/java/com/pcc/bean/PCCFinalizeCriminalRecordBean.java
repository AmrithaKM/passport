package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PCCFinalizeCriminalRecordBean implements Serializable {
    private String id;
    private String isCriminal;
    private String updatedBy;
    private String updateTimeStamp;
    private String ipAddress;

    public String getId() {
        return id;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getIpAddress() { return ipAddress; }

    public String getIsCriminal() {
        return isCriminal;
    }
}
