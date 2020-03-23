package com.pcc.states;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;
import java.util.List;

@CordaSerializable

public class PCCApplicationDetailsState implements Serializable{
    private String uid;
    private String userId;
    private String updateTimeStamp;
    private String ipAddress;

    /*User's Personal Details Starts*/
    private String firstName;
    private String middleName;
    private String lastName;
    private String parentName;
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

    private String identityProofType;
    private String identityProofId;
    /*User's Personal Details Ends*/

    /*User's Address Details Starts*/
    //present
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

    //permanent
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

    private String addressProofType;
    private String addressProofId;
    /*User's Address Details Ends*/

    private String typeOfPCC;
    private String description;
    private String fieldDescription;
    private String finalRemarks;

    private String purpose;
    private String submittedTo;

    /*Previous Address Details Starts*/
    private String previousPCCNumber;
    private String previousPCCDateTaken;
    private String previousPCCPurpose;
    /*Previous Address Details Ends*/

    /*Criminal record starts*/
    private List<?> listCriminalHistoryState;
    /*Criminal record ends*/

    /*Physical verification flags start*/
    private Boolean flagQuestion1;
    private Boolean flagQuestion2;
    private Boolean flagQuestion3;
    private Boolean flagQuestion4;
    private Boolean flagQuestion5;
    private Boolean flagQuestion6;
    /*Physical verification flags end*/

    public PCCApplicationDetailsState() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(String updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRelativeName() {
        return relativeName;
    }

    public void setRelativeName(String relativeName) {
        this.relativeName = relativeName;
    }

    public String getMobileNumber1() {
        return mobileNumber1;
    }

    public void setMobileNumber1(String mobileNumber1) {
        this.mobileNumber1 = mobileNumber1;
    }

    public String getMobileNumber2() {
        return mobileNumber2;
    }

    public void setMobileNumber2(String mobileNumber2) {
        this.mobileNumber2 = mobileNumber2;
    }

    public String getLandlineNumber1() {
        return landlineNumber1;
    }

    public void setLandlineNumber1(String landlineNumber1) {
        this.landlineNumber1 = landlineNumber1;
    }

    public String getLandlineNumber2() {
        return landlineNumber2;
    }

    public void setLandlineNumber2(String landlineNumber2) {
        this.landlineNumber2 = landlineNumber2;
    }

    public String getLandlineNumber3() {
        return landlineNumber3;
    }

    public void setLandlineNumber3(String landlineNumber3) {
        this.landlineNumber3 = landlineNumber3;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getIdentityProofType() {
        return identityProofType;
    }

    public void setIdentityProofType(String identityProofType) {
        this.identityProofType = identityProofType;
    }

    public String getIdentityProofId() {
        return identityProofId;
    }

    public void setIdentityProofId(String identityProofId) {
        this.identityProofId = identityProofId;
    }

    public String getPresentHouseNo() {
        return presentHouseNo;
    }

    public void setPresentHouseNo(String presentHouseNo) {
        this.presentHouseNo = presentHouseNo;
    }

    public String getPresentStreetName() {
        return presentStreetName;
    }

    public void setPresentStreetName(String presentStreetName) {
        this.presentStreetName = presentStreetName;
    }

    public String getPresentColonyLocalArea() {
        return presentColonyLocalArea;
    }

    public void setPresentColonyLocalArea(String presentColonyLocalArea) {
        this.presentColonyLocalArea = presentColonyLocalArea;
    }

    public String getPresentVillageTownCity() {
        return presentVillageTownCity;
    }

    public void setPresentVillageTownCity(String presentVillageTownCity) {
        this.presentVillageTownCity = presentVillageTownCity;
    }

    public String getPresentTehsilBlockMandal() {
        return presentTehsilBlockMandal;
    }

    public void setPresentTehsilBlockMandal(String presentTehsilBlockMandal) {
        this.presentTehsilBlockMandal = presentTehsilBlockMandal;
    }

    public String getPresentCountry() {
        return presentCountry;
    }

    public void setPresentCountry(String presentCountry) {
        this.presentCountry = presentCountry;
    }

    public String getPresentState() {
        return presentState;
    }

    public void setPresentState(String presentState) {
        this.presentState = presentState;
    }

    public String getPresentDistrict() {
        return presentDistrict;
    }

    public void setPresentDistrict(String presentDistrict) {
        this.presentDistrict = presentDistrict;
    }

    public String getPresentPoliceStation() {
        return presentPoliceStation;
    }

    public void setPresentPoliceStation(String presentPoliceStation) {
        this.presentPoliceStation = presentPoliceStation;
    }

    public String getPresentPinCode() {
        return presentPinCode;
    }

    public void setPresentPinCode(String presentPinCode) {
        this.presentPinCode = presentPinCode;
    }

    public String getPermanentHouseNo() {
        return permanentHouseNo;
    }

    public void setPermanentHouseNo(String permanentHouseNo) {
        this.permanentHouseNo = permanentHouseNo;
    }

    public String getPermanentStreetName() {
        return permanentStreetName;
    }

    public void setPermanentStreetName(String permanentStreetName) {
        this.permanentStreetName = permanentStreetName;
    }

    public String getPermanentColonyLocalArea() {
        return permanentColonyLocalArea;
    }

    public void setPermanentColonyLocalArea(String permanentColonyLocalArea) {
        this.permanentColonyLocalArea = permanentColonyLocalArea;
    }

    public String getPermanentVillageTownCity() {
        return permanentVillageTownCity;
    }

    public void setPermanentVillageTownCity(String permanentVillageTownCity) {
        this.permanentVillageTownCity = permanentVillageTownCity;
    }

    public String getPermanentTehsilBlockMandal() {
        return permanentTehsilBlockMandal;
    }

    public void setPermanentTehsilBlockMandal(String permanentTehsilBlockMandal) {
        this.permanentTehsilBlockMandal = permanentTehsilBlockMandal;
    }

    public String getPermanentCountry() {
        return permanentCountry;
    }

    public void setPermanentCountry(String permanentCountry) {
        this.permanentCountry = permanentCountry;
    }

    public String getPermanentState() {
        return permanentState;
    }

    public void setPermanentState(String permanentState) {
        this.permanentState = permanentState;
    }

    public String getPermanentDistrict() {
        return permanentDistrict;
    }

    public void setPermanentDistrict(String permanentDistrict) {
        this.permanentDistrict = permanentDistrict;
    }

    public String getPermanentPoliceStation() {
        return permanentPoliceStation;
    }

    public void setPermanentPoliceStation(String permanentPoliceStation) {
        this.permanentPoliceStation = permanentPoliceStation;
    }

    public String getPermanentPinCode() {
        return permanentPinCode;
    }

    public void setPermanentPinCode(String permanentPinCode) {
        this.permanentPinCode = permanentPinCode;
    }

    public String getAddressProofType() {
        return addressProofType;
    }

    public void setAddressProofType(String addressProofType) {
        this.addressProofType = addressProofType;
    }

    public String getAddressProofId() {
        return addressProofId;
    }

    public void setAddressProofId(String addressProofId) {
        this.addressProofId = addressProofId;
    }

    public String getTypeOfPCC() {
        return typeOfPCC;
    }

    public void setTypeOfPCC(String typeOfPCC) {
        this.typeOfPCC = typeOfPCC;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    } 

    public String getFinalRemarks() {
        return finalRemarks;
    }

    public void setFinalRemarks(String finalRemarks) {
        this.finalRemarks = finalRemarks;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSubmittedTo() {
        return submittedTo;
    }

    public void setSubmittedTo(String submittedTo) {
        this.submittedTo = submittedTo;
    }

    public String getPreviousPCCNumber() {
        return previousPCCNumber;
    }

    public void setPreviousPCCNumber(String previousPCCNumber) {
        this.previousPCCNumber = previousPCCNumber;
    }

    public String getPreviousPCCDateTaken() {
        return previousPCCDateTaken;
    }

    public void setPreviousPCCDateTaken(String previousPCCDateTaken) {
        this.previousPCCDateTaken = previousPCCDateTaken;
    }

    public String getPreviousPCCPurpose() {
        return previousPCCPurpose;
    }

    public void setPreviousPCCPurpose(String previousPCCPurpose) {
        this.previousPCCPurpose = previousPCCPurpose;
    }

    public Boolean getFlagQuestion1() {
        return flagQuestion1;
    }

    public void setFlagQuestion1(Boolean flagQuestion1) {
        this.flagQuestion1 = flagQuestion1;
    }

    public Boolean getFlagQuestion2() {
        return flagQuestion2;
    }

    public void setFlagQuestion2(Boolean flagQuestion2) {
        this.flagQuestion2 = flagQuestion2;
    }

    public Boolean getFlagQuestion3() {
        return flagQuestion3;
    }

    public void setFlagQuestion3(Boolean flagQuestion3) {
        this.flagQuestion3 = flagQuestion3;
    }

    public Boolean getFlagQuestion4() {
        return flagQuestion4;
    }

    public void setFlagQuestion4(Boolean flagQuestion4) {
        this.flagQuestion4 = flagQuestion4;
    }

    public Boolean getFlagQuestion5() {
        return flagQuestion5;
    }

    public void setFlagQuestion5(Boolean flagQuestion5) {
        this.flagQuestion5 = flagQuestion5;
    }

    public Boolean getFlagQuestion6() {
        return flagQuestion6;
    }

    public void setFlagQuestion6(Boolean flagQuestion6) {
        this.flagQuestion6 = flagQuestion6;
    }

    public List<?> getListCriminalHistoryState() {
        return listCriminalHistoryState;
    }

    public void setListCriminalHistoryState(List<?> listCriminalHistoryState) {
        this.listCriminalHistoryState = listCriminalHistoryState;
    }
}
