package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PCCApproveOrRejectBean implements Serializable {
    private String id;
	private String isApproved;
    private String updatedBy;
    private String updateTimeStamp;
    private String ipAddress;
    private String finalRemarks;
    private String filePath;

    public String getId() {
        return id;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getIpAddress() { return ipAddress; }

    public String getFinalRemarks() {
        return finalRemarks;
    }

    public String getFilePath() { return filePath; }
}
