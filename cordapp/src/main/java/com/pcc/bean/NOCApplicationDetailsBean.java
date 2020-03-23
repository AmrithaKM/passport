package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class NOCApplicationDetailsBean implements Serializable {
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

    private String identityProofId;
    private String identityProofType;

    private String fileType;
    private String fileSubType;
    private String fileId;
    private String fileImage;

    //4 new attachments
    private String certificateOfRegistration;
    private String certificateOfInsurance;
    private String certificateRCOwnerDrivingLicense;
    private String certificatePUC;

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

    private String typeOfNOC;
    private String description;

    private String purpose;
    private String submittedTo;

    /*Vehicle details start*/
    private String typeOfVehicle;
    private String registrationNumber;
    private String make;
    private String model;
    private String engineNumber;
    private String chasisNumber;
    private String registeredInAuthority;
    private String periodInState;
    private String motorTaxPaidUpto;
    private Boolean anyTaxPending;
    private Boolean vehicleInvolvedInTheftCase;
    private Boolean actionUnderMotorVehicleAct;
    private Boolean involvedTransportProhibitedGoods;
    /*Vehicle details end*/

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

    public String getIdentityProofId() {
        return identityProofId;
    }

    public String getIdentityProofType() {
        return identityProofType;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileSubType() {
        return fileSubType;
    }

    public String getFileId() {
        return fileId;
    }



    public String getFileImage() {
        return fileImage;
    }

    public String getCertificateOfRegistration() {
        return certificateOfRegistration;
    }

    public String getCertificateOfInsurance() {
        return certificateOfInsurance;
    }

    public String getCertificateRCOwnerDrivingLicense() {
        return certificateRCOwnerDrivingLicense;
    }

    public String getCertificatePUC() {
        return certificatePUC;
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

    public String getTypeOfNOC() {
        return typeOfNOC;
    }

    public String getDescription() {
        return description;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getSubmittedTo() {
        return submittedTo;
    }

    public String getTypeOfVehicle() {
        return typeOfVehicle;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public String getChasisNumber() {
        return chasisNumber;
    }

    public String getRegisteredInAuthority() {
        return registeredInAuthority;
    }

    public String getPeriodInState() {
        return periodInState;
    }

    public String getMotorTaxPaidUpto() {
        return motorTaxPaidUpto;
    }

    public Boolean getAnyTaxPending() {
        return anyTaxPending;
    }

    public Boolean getVehicleInvolvedInTheftCase() {
        return vehicleInvolvedInTheftCase;
    }

    public Boolean getActionUnderMotorVehicleAct() {
        return actionUnderMotorVehicleAct;
    }

    public Boolean getInvolvedTransportProhibitedGoods() {
        return involvedTransportProhibitedGoods;
    }
}
