package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PCCSavePDFBean implements Serializable {
    private String id;
	private String filePath;
    private String updatedBy;
    private String updateTimeStamp;
    private String ipAddress;


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
}
