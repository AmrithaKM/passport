package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PassportFieldReportBean implements Serializable {
    private String id;
    private String isAdverse;
    private String fvoDate;
    private String fvo;
    private String fieldQuestion;
    private String fieldAnswer;
    private String fieldRemarks;
    private String updatedBy;
    private String updateTimeStamp;

    public String getId() {
        return id;
    }

    public String getIsAdverse() {
        return isAdverse;
    }

    public String getFvoDate() {
        return fvoDate;
    }

    public String getFvo() {
        return fvo;
    }

    public String getFieldQuestion() {
        return fieldQuestion;
    }

    public String getFieldAnswer() {
        return fieldAnswer;
    }

    public String getFieldRemarks() {
        return fieldRemarks;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }
}
