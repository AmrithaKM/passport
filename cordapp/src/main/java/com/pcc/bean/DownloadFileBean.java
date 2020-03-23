package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class DownloadFileBean implements Serializable {
    private String fileURL;
    private String fileType;
    private String destinationFilePath;

    public String getFileURL() {
        return fileURL;
    }

    public String getFileType() { return fileType; }

    public String getDestinationFilePath() {
        return destinationFilePath;
    }
}
