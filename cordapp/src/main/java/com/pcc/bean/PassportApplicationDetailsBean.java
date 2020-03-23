package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PassportApplicationDetailsBean implements Serializable {
    private String uid;
    private String userId;
    private String updateTimeStamp;
    private String ipAddress;

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
    private String fileVerificationMode;

    /*User's Personal Details Starts*/
    private String firstName;
    private String middleName;
    private String lastName;
    private String emailId;

    private String mobileNumber;

    private String gender;
    private String dob;
    private String parentName;
    /*User's Personal Details Ends*/

    /*User's Address Details Starts*/
    private String verificationAddress;
    private String permanentAddress;

    private String policeStation;

    private String description;
    private String fieldDescription;
    private String finalRemarks;

    private String purpose;
    private String submittedTo;

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

    public String getFileNumber() {
        return fileNumber;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getDphqIdName() {
        return dphqIdName;
    }

    public String getPvRequestId() {
        return pvRequestId;
    }

    public String getPvInitiationDate() {
        return pvInitiationDate;
    }

    public String getPvRequestStatus() {
        return pvRequestStatus;
    }

    public String getPvSequenceNumber() {
        return pvSequenceNumber;
    }

    public String getFieldVerificationMode() {
        return fieldVerificationMode;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public String getSpouceName() {
        return spouceName;
    }

    public String getFileVerificationMode() {
        return fileVerificationMode;
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

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getParentName() {
        return parentName;
    }

    public String getVerificationAddress() {
        return verificationAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public String getDescription() {
        return description;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public String getFinalRemarks() {
        return finalRemarks;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getSubmittedTo() {
        return submittedTo;
    }
}
