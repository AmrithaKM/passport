package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class CCTNSCriminalHistoryAddressBean implements Serializable {
    private String addressType;
    private String houseNo;
    private String streetName;
    private String colonyLocalArea;
    private String villageTownCity;
    private String tehsilBlockMandal;
    private String country;
    private String state;
    private String district;
    private String pinCode;
    private String policeStation;

    private String isCurrent;

    public String getAddressType() {
        return addressType;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getColonyLocalArea() {
        return colonyLocalArea;
    }

    public String getVillageTownCity() {
        return villageTownCity;
    }

    public String getTehsilBlockMandal() {
        return tehsilBlockMandal;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getDistrict() {
        return district;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public String getIsCurrent() {
        return isCurrent;
    }
}
