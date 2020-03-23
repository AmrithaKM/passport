package com.pcc.states;

import net.corda.core.serialization.CordaSerializable;

import java.util.List;

@CordaSerializable

public class PassportApplicationDetailsState {
    private String uid;
    private String userId;
    private String updateTimeStamp;
    private String ipAddress;
    private Double latitude;
    private Double longitude;
    private String locationName;

    private String fileNumber;
    private String activityDate;
    private String activityType;
    private String dphqIdName;
    private String pvRequestId;
    private String pvInitiationDate;
    private String pvRequestStatus;
    private String pvSequenceNumber;
    private String fieldVerificationMode;
    private String placeOfBirth;
    private String spouceName;
    private String fileVerificatinMode;

    /*User's Personal Details Starts*/
    private String firstName;
    private String middleName;
    private String lastName;
    private String parentName;
    private String emailId;
    private String mobileNumber;
    private String gender;
    private String dob;
    private String adharNo;
    /*User's Personal Details Ends*/

    /*User's Address Details Starts*/
    private String verificationAddress;
    private String permanentAddress;
    private String policeStation;
    /*User's Address Details Ends*/

    private String description;
    private String fieldDescription;
    private String finalRemarks;

    private String purpose;
    private String submittedTo;


    /*Criminal record starts*/
    private List<?> listCriminalHistoryState;
    /*Criminal record ends*/

    /*Physical verification flags start*/
    private PassportPhysicalVerificationState passportPhysicalVerificationStateFVO;
    private PassportPhysicalVerificationState passportPhysicalVerificationStateDSBO;
    /*Physical verification flags end*/

    public PassportApplicationDetailsState() {

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

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDphqIdName() {
        return dphqIdName;
    }

    public void setDphqIdName(String dphqIdName) {
        this.dphqIdName = dphqIdName;
    }

    public String getPvRequestId() {
        return pvRequestId;
    }

    public void setPvRequestId(String pvRequestId) {
        this.pvRequestId = pvRequestId;
    }

    public String getPvInitiationDate() {
        return pvInitiationDate;
    }

    public void setPvInitiationDate(String pvInitiationDate) {
        this.pvInitiationDate = pvInitiationDate;
    }

    public String getPvRequestStatus() {
        return pvRequestStatus;
    }

    public void setPvRequestStatus(String pvRequestStatus) {
        this.pvRequestStatus = pvRequestStatus;
    }

    public String getPvSequenceNumber() {
        return pvSequenceNumber;
    }

    public void setPvSequenceNumber(String pvSequenceNumber) {
        this.pvSequenceNumber = pvSequenceNumber;
    }

    public String getFieldVerificationMode() {
        return fieldVerificationMode;
    }

    public void setFieldVerificationMode(String fieldVerificationMode) {
        this.fieldVerificationMode = fieldVerificationMode;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getSpouceName() {
        return spouceName;
    }

    public void setSpouceName(String spouceName) {
        this.spouceName = spouceName;
    }

    public String getFileVerificatinMode() {
        return fileVerificatinMode;
    }

    public void setFileVerificatinMode(String fileVerificatinMode) {
        this.fileVerificatinMode = fileVerificatinMode;
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

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getAdharNo() {
        return adharNo;
    }

    public void setAdharNo(String adharNo) {
        this.adharNo = adharNo;
    }

    public String getVerificationAddress() {
        return verificationAddress;
    }

    public void setVerificationAddress(String verificationAddress) {
        this.verificationAddress = verificationAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
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

    public List<?> getListCriminalHistoryState() {
        return listCriminalHistoryState;
    }

    public void setListCriminalHistoryState(List<?> listCriminalHistoryState) {
        this.listCriminalHistoryState = listCriminalHistoryState;
    }

    public PassportPhysicalVerificationState getPassportPhysicalVerificationStateFVO() {
        return passportPhysicalVerificationStateFVO;
    }

    public void setPassportPhysicalVerificationStateFVO(PassportPhysicalVerificationState passportPhysicalVerificationStateFVO) {
        this.passportPhysicalVerificationStateFVO = passportPhysicalVerificationStateFVO;
    }

    public PassportPhysicalVerificationState getPassportPhysicalVerificationStateDSBO() {
        return passportPhysicalVerificationStateDSBO;
    }

    public void setPassportPhysicalVerificationStateDSBO(PassportPhysicalVerificationState passportPhysicalVerificationStateDSBO) {
        this.passportPhysicalVerificationStateDSBO = passportPhysicalVerificationStateDSBO;
    }
}
