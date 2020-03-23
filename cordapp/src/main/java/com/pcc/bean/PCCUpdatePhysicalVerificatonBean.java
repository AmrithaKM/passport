package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PCCUpdatePhysicalVerificatonBean implements Serializable {
    private String id;
    private String filePath;
    private String fieldDescription;
    private String updatedBy;
    private String updateTimeStamp;
    private String ipAddress;
    private String identificationNumber;
    private String flagQuestion1;
    private String flagQuestion2;
    private String flagQuestion3;
    private String flagQuestion4;
    private String flagQuestion5;
    private String flagQuestion6;

    private String remarkQuestion1;
    private String remarkQuestion2;
    private String remarkQuestion3;
    private String remarkQuestion4;
    private String remarkQuestion5;
    private String remarkQuestion6;

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getIpAddress() { return ipAddress; }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getFlagQuestion1() {
        return flagQuestion1;
    }

    public String getFlagQuestion2() {
        return flagQuestion2;
    }

    public String getFlagQuestion3() {
        return flagQuestion3;
    }

    public String getFlagQuestion4() {
        return flagQuestion4;
    }

    public String getFlagQuestion5() {
        return flagQuestion5;
    }

    public String getFlagQuestion6() {
        return flagQuestion6;
    }

    public String getRemarkQuestion1() {
        return remarkQuestion1;
    }

    public String getRemarkQuestion2() {
        return remarkQuestion2;
    }

    public String getRemarkQuestion3() {
        return remarkQuestion3;
    }

    public String getRemarkQuestion4() {
        return remarkQuestion4;
    }

    public String getRemarkQuestion5() {
        return remarkQuestion5;
    }

    public String getRemarkQuestion6() {
        return remarkQuestion6;
    }
}
