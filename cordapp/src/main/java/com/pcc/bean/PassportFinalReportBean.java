package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PassportFinalReportBean implements Serializable {
    private String id;
	private String isAdverse;
	private String finalReportDate;
    private String updatedBy;
    private String updateTimeStamp;

    public String getId() {
        return id;
    }

    public String getIsAdverse() {
        return isAdverse;
    }

    public String getFinalReportDate() {
        return finalReportDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }
}
