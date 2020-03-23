package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;
import java.util.List;

@CordaSerializable
public class CCTNSCriminalHistoryBean implements Serializable {
    private String firstName;
    private String middleName;
    private String lastName;
    private String relativeName;
    private List<CCTNSCriminalHistoryAddressBean> listCriminalHistoryAddressBean;
    private String fir_reg_num;
    private String fir_district_cd;
    private String fir_ps_cd;
    private String firDisplay;
    private String accused_srno;
    private String address_srno;
    private String isSelected;

    private List<CCTNSCriminalHistoryFIRBean> listCriminalHistoryFIRBean;
    public List<CCTNSCriminalHistoryAddressBean> getListCriminalHistoryAddressBean() { return listCriminalHistoryAddressBean; }

    public List<CCTNSCriminalHistoryFIRBean> getListCriminalHistoryFIRBean() {
        return listCriminalHistoryFIRBean;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRelativeName() {
        return relativeName;
    }

    public String getFir_reg_num() {
        return fir_reg_num;
    }

    public String getFir_district_cd() {
        return fir_district_cd;
    }

    public String getFir_ps_cd() {
        return fir_ps_cd;
    }

    public String getFirDisplay() {
        return firDisplay;
    }

    public String getAccused_srno() {
        return accused_srno;
    }

    public String getAddress_srno() {
        return address_srno;
    }

    public String getIsSelected() {
        return isSelected;
    }
}
