package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;
import java.util.List;

@CordaSerializable
public class PassportUpdateCriminalHistoryBean implements Serializable {
    private String id;
    private List<CCTNSCriminalHistoryBean> listCriminalHistoryBean;
    private String updatedBy;
    private String updateTimeStamp;
    private String ipAddress;

    public String getId() {
        return id;
    }

    public List<CCTNSCriminalHistoryBean> getListCriminalHistoryBean() {
        return listCriminalHistoryBean;
    }

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
