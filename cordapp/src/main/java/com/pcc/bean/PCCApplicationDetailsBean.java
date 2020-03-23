package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PCCApplicationDetailsBean implements Serializable {
    private String uid;
    private String userId;
    private String updateTimeStamp;
    private String ipAddress;

    /*User's Personal Details Starts*/
    private String firstName;
    private String middleName;
    private String lastName;
    private String nationality;
    private String emailId;
    private String relationType;
    private String relativeName;
    private String mobileNumber1;
    private String mobileNumber2;
    private String landlineNumber1;
    private String landlineNumber2;
    private String landlineNumber3;
    private String gender;
    private String dob;
    private String parentName;

    private String identityProofType;
    private String identityProofId;
    private String identityProofImage;
    /*User's Personal Details Ends*/

    /*User's Address Details Starts*/
    //Present
    private String presentHouseNo;
    private String presentStreetName;
    private String presentColonyLocalArea;
    private String presentVillageTownCity;
    private String presentTehsilBlockMandal;
    private String presentCountry;
    private String presentState;
    private String presentDistrict;
    private String presentPoliceStation;
    private String presentPinCode;

    //Permanent
    private String permanentHouseNo;
    private String permanentStreetName;
    private String permanentColonyLocalArea;
    private String permanentVillageTownCity;
    private String permanentTehsilBlockMandal;
    private String permanentCountry;
    private String permanentState;
    private String permanentDistrict;
    private String permanentPoliceStation;
    private String permanentPinCode;

    //Image details
    private String addressProofType;
    private String addressProofId;
    private String addressProofImage;
    /*User's Address Details Ends*/

    private String typeOfPCC;
    private String description;
    private String fieldDescription;
    private String finalRemarks;

    private String purpose;
    private String submittedTo;

    private String previousPCCNumber;
    private String previousPCCDateTaken;
    private String previousPCCPurpose;

    public String getUid() {
        return uid;
    }

    public String getUserId() {
        return userId;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getIpAddress() {
        return ipAddress;
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

    public String getNationality() {
        return nationality;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getRelationType() {
        return relationType;
    }

    public String getRelativeName() {
        return relativeName;
    }

    public String getMobileNumber1() {
        return mobileNumber1;
    }

    public String getMobileNumber2() {
        return mobileNumber2;
    }

    public String getLandlineNumber1() {
        return landlineNumber1;
    }

    public String getLandlineNumber2() {
        return landlineNumber2;
    }

    public String getLandlineNumber3() {
        return landlineNumber3;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getIdentityProofType() {
        return identityProofType;
    }

    public String getIdentityProofId() {
        return identityProofId;
    }

    public String getIdentityProofImage() {
        return identityProofImage;
    }

    public String getPresentHouseNo() {
        return presentHouseNo;
    }

    public String getPresentStreetName() {
        return presentStreetName;
    }

    public String getPresentColonyLocalArea() {
        return presentColonyLocalArea;
    }

    public String getPresentVillageTownCity() {
        return presentVillageTownCity;
    }

    public String getPresentTehsilBlockMandal() {
        return presentTehsilBlockMandal;
    }

    public String getPresentCountry() {
        return presentCountry;
    }

    public String getPresentState() {
        return presentState;
    }

    public String getPresentDistrict() {
        return presentDistrict;
    }

    public String getPresentPoliceStation() {
        return presentPoliceStation;
    }

    public String getPresentPinCode() {
        return presentPinCode;
    }

    public String getPermanentHouseNo() {
        return permanentHouseNo;
    }

    public String getPermanentStreetName() {
        return permanentStreetName;
    }

    public String getPermanentColonyLocalArea() {
        return permanentColonyLocalArea;
    }

    public String getPermanentVillageTownCity() {
        return permanentVillageTownCity;
    }

    public String getPermanentTehsilBlockMandal() {
        return permanentTehsilBlockMandal;
    }

    public String getPermanentCountry() {
        return permanentCountry;
    }

    public String getPermanentState() {
        return permanentState;
    }

    public String getPermanentDistrict() {
        return permanentDistrict;
    }

    public String getPermanentPoliceStation() {
        return permanentPoliceStation;
    }

    public String getPermanentPinCode() {
        return permanentPinCode;
    }

    public String getAddressProofType() {
        return addressProofType;
    }

    public String getAddressProofId() {
        return addressProofId;
    }

    public String getAddressProofImage() {
        return addressProofImage;
    }

    public String getTypeOfPCC() {
        return typeOfPCC;
    }

    public String getDescription() {
        return description;
    }
    
    public String getFinalRemarks() {
        return finalRemarks;
    } 
    
     public String getFieldDescription() {
        return fieldDescription;
    } 

    public String getPurpose() {
        return purpose;
    }

    public String getSubmittedTo() {
        return submittedTo;
    }

    public String getParentName() {
        return parentName;
    }

    public String getPreviousPCCNumber() {
        return previousPCCNumber;
    }

    public String getPreviousPCCDateTaken() {
        return previousPCCDateTaken;
    }

    public String getPreviousPCCPurpose() {
        return previousPCCPurpose;
    }
}
