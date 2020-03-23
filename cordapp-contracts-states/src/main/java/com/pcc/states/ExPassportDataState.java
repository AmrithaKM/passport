package com.pcc.states;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ExPassportDataState implements LinearState {
    private final Party owner;
    private final String policeStation;
    private final String fileNumber;
    private final String pvRequestId;
    private final String applicantName;
    private final String gender;
    private final String dob;
    private final String spouseName;
    private final String fatherName;
    private final String pvInitiationDate;
    private final String verificationAddress;
    private final String permanentAddress;
    private final String pvSequenceNo;
    private final String emailId;
    private final String phoneNumber;

    private final Boolean isAdverseByDCRB;
    private final String dateOfDCRB;
    private final String crimeNo;
    private final String crimeYear;
    private final String crimePoliceStation;
    private final String crimeUnderSection;
    private final String crimeRemarks;

    private final Boolean isAdverseByEVIP;
    private final String dateOfFVO;
    private final String fvo;
    private final String fieldQuestion;
    private final String fieldAnswer;
    private final String fieldRemarks;

    private final Boolean isAdverseByFinalReport;
    private final String finalReportDate;

    private final String updatedBy;
    private final String updateTimestamp;
    private final List<AbstractParty> listOfListeners;

    private final Boolean isApproved;
    private final SecureHash secureHash;
    private final UniqueIdentifier linearId;

    public ExPassportDataState(Party owner,
                             String policeStation,
                             String fileNumber,
                             String pvRequestId,
                             String applicantName,
                             String gender,
                             String dob,
                             String spouseName,
                             String fatherName,
                             String pvInitiationDate,
                             String verificationAddress,
                             String permanentAddress,
                             String pvSequenceNo,
                             String emailId,
                             String phoneNumber,
                             String updatedBy,
                             String updateTimestamp,
                             List<AbstractParty> listOfListeners) {
        this.owner = owner;
        this.policeStation = policeStation;
        this.fileNumber = fileNumber;
        this.pvRequestId = pvRequestId;
        this.applicantName = applicantName;
        this.gender = gender;
        this.dob = dob;
        this.spouseName = spouseName;
        this.fatherName = fatherName;
        this.pvInitiationDate = pvInitiationDate;
        this.verificationAddress = verificationAddress;
        this.permanentAddress = permanentAddress;
        this.pvSequenceNo = pvSequenceNo;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;

        this.isAdverseByDCRB = null;
        this.dateOfDCRB = null;
        this.crimeNo = null;
        this.crimeYear = null;
        this.crimePoliceStation = null;
        this.crimeUnderSection = null;
        this.crimeRemarks = null;

        this.isAdverseByEVIP = null;
        this.dateOfFVO = null;
        this.fvo = null;
        this.fieldQuestion = null;
        this.fieldAnswer = null;
        this.fieldRemarks = null;

        this.isAdverseByFinalReport = null;
        this.finalReportDate = null;

        this.listOfListeners = listOfListeners;
        this.updatedBy = updatedBy;
        this.updateTimestamp = updateTimestamp;

        this.isApproved = null;
        this.secureHash = null;
        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public ExPassportDataState(Party owner,
                             String policeStation,
                             String fileNumber,
                             String pvRequestId,
                             String applicantName,
                             String gender,
                             String dob,
                             String spouseName,
                             String fatherName,
                             String pvInitiationDate,
                             String verificationAddress,
                             String permanentAddress,
                             String pvSequenceNo,
                             String emailId,
                             String phoneNumber,

                             Boolean isAdverseByDCRB,
                             String dateOfDCRB,
                             String crimeNo,
                             String crimeYear,
                             String crimePoliceStation,
                             String crimeUnderSection,
                             String crimeRemarks,

                             Boolean isAdverseByEVIP,
                             String dateOfFVO,
                             String fvo,
                             String fieldQuestion,
                             String fieldAnswer,
                             String fieldRemarks,

                             Boolean isAdverseByFinalReport,
                             String finalReportDate,

                             List<AbstractParty> listOfListeners,
                             Boolean isApproved,
                             String updatedBy,
                             String updateTimestamp,
                             SecureHash secureHash,
                             UniqueIdentifier linearId) {
        this.owner = owner;
        this.policeStation = policeStation;
        this.fileNumber = fileNumber;
        this.pvRequestId = pvRequestId;
        this.applicantName = applicantName;
        this.gender = gender;
        this.dob = dob;
        this.spouseName = spouseName;
        this.fatherName = fatherName;
        this.pvInitiationDate = pvInitiationDate;
        this.verificationAddress = verificationAddress;
        this.permanentAddress = permanentAddress;
        this.pvSequenceNo = pvSequenceNo;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;

        this.isAdverseByDCRB = isAdverseByDCRB;
        this.dateOfDCRB = dateOfDCRB;
        this.crimeNo = crimeNo;
        this.crimeYear = crimeYear;
        this.crimePoliceStation = crimePoliceStation;
        this.crimeUnderSection = crimeUnderSection;
        this.crimeRemarks = crimeRemarks;

        this.isAdverseByEVIP = isAdverseByEVIP;
        this.dateOfFVO = dateOfFVO;
        this.fvo = fvo;
        this.fieldQuestion = fieldQuestion;
        this.fieldAnswer = fieldAnswer;
        this.fieldRemarks = fieldRemarks;

        this.isAdverseByFinalReport = isAdverseByFinalReport;
        this.finalReportDate = finalReportDate;

        this.listOfListeners = listOfListeners;
        this.updatedBy = updatedBy;
        this.updateTimestamp = updateTimestamp;
        this.isApproved = isApproved;
        this.secureHash = secureHash;
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return listOfListeners;
    }

    public Party getOwner() {
        return owner;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public String getPvRequestId() {
        return pvRequestId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getPvInitiationDate() {
        return pvInitiationDate;
    }

    public String getVerificationAddress() {
        return verificationAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public String getPvSequenceNo() {
        return pvSequenceNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Boolean getAdverseByDCRB() {
        return isAdverseByDCRB;
    }

    public String getDateOfDCRB() {
        return dateOfDCRB;
    }

    public String getCrimeNo() {
        return crimeNo;
    }

    public String getCrimeYear() {
        return crimeYear;
    }

    public String getCrimePoliceStation() {
        return crimePoliceStation;
    }

    public String getCrimeUnderSection() {
        return crimeUnderSection;
    }

    public String getCrimeRemarks() {
        return crimeRemarks;
    }

    public Boolean getAdverseByEVIP() {
        return isAdverseByEVIP;
    }

    public String getDateOfFVO() {
        return dateOfFVO;
    }

    public String getFvo() {
        return fvo;
    }

    public String getFieldQuestion() {
        return fieldQuestion;
    }

    public String getFieldAnswer() {
        return fieldAnswer;
    }

    public String getFieldRemarks() {
        return fieldRemarks;
    }

    public Boolean getIsAdverseByFinalReport() {
        return isAdverseByFinalReport;
    }

    public String getFinalReportDate() {
        return finalReportDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateTimestamp() {
        return updateTimestamp;
    }

    public List<AbstractParty> getListOfListeners() {
        return listOfListeners;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public SecureHash getSecureHash() {
        return secureHash;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public ExPassportDataState uploadDCRBReport(Boolean isAdverse,
                                              String eDCRBReportDate,
                                              String updatedCrimeNo,
                                              String updatedCrimePoliceStation,
                                              String updatedCrimeYear,
                                              String updatedCrimeUnderSection,
                                              String updatedCrimeRemarks,
                                              String updatedBy,
                                              String updateTimestamp) {
        return new ExPassportDataState(owner,
                policeStation,
                fileNumber,
                pvRequestId,
                applicantName,
                gender,
                dob,
                spouseName,
                fatherName,
                pvInitiationDate,
                verificationAddress,
                permanentAddress,
                pvSequenceNo,
                emailId,
                phoneNumber,

                isAdverse, //isAdverseByDCRB,
                eDCRBReportDate, //dateOfDCRB,
                updatedCrimeNo,
                updatedCrimeYear,
                updatedCrimePoliceStation,
                updatedCrimeUnderSection,
                updatedCrimeRemarks,

                isAdverseByEVIP,
                dateOfFVO,
                fvo,
                fieldQuestion,
                fieldAnswer,
                fieldRemarks,

                isAdverseByFinalReport,
                finalReportDate,

                listOfListeners,
                isApproved,
                updatedBy,
                updateTimestamp,
                secureHash,
                linearId);
    }

    public ExPassportDataState uploadFieldReport(Boolean isAdverse,
                                               String updatedFvoReportDate,
                                               String updatedFvo,
                                               String updatedFieldQuestion,
                                               String updatedFieldAnswer,
                                               String updatedFieldRemarks,
                                               String updatedBy,
                                               String updateTimestamp) {
        return new ExPassportDataState(owner,
                policeStation,
                fileNumber,
                pvRequestId,
                applicantName,
                gender,
                dob,
                spouseName,
                fatherName,
                pvInitiationDate,
                verificationAddress,
                permanentAddress,
                pvSequenceNo,
                emailId,
                phoneNumber,

                isAdverseByDCRB,
                dateOfDCRB,
                crimeNo,
                crimeYear,
                crimePoliceStation,
                crimeUnderSection,
                crimeRemarks,

                isAdverse, //isAdverseByEVIP,
                updatedFvoReportDate, //dateOfFVO,
                updatedFvo, //fvo,
                updatedFieldQuestion, //eVIPQuestion,
                updatedFieldAnswer,
                updatedFieldRemarks,

                isAdverseByFinalReport,
                finalReportDate,

                listOfListeners,
                isApproved,
                updatedBy,
                updateTimestamp,
                secureHash,
                linearId);
    }

    public ExPassportDataState uploadFinalReport(Boolean isAdverse,
                                               String updatedFinalReportDate,
                                               String updatedBy,
                                               String updateTimestamp) {
        return new ExPassportDataState(owner,
                policeStation,
                fileNumber,
                pvRequestId,
                applicantName,
                gender,
                dob,
                spouseName,
                fatherName,
                pvInitiationDate,
                verificationAddress,
                permanentAddress,
                pvSequenceNo,
                emailId,
                phoneNumber,

                isAdverseByDCRB,
                dateOfDCRB,
                crimeNo,
                crimeYear,
                crimePoliceStation,
                crimeUnderSection,
                crimeRemarks,

                isAdverseByEVIP,
                dateOfFVO,
                fvo,
                fieldQuestion,
                fieldAnswer,
                fieldRemarks,

                isAdverse, //isAdverseByFinalReport,
                updatedFinalReportDate, //finalReportDate,

                listOfListeners,
                isApproved,
                updatedBy,
                updateTimestamp,
                secureHash,
                linearId);
    }

    public ExPassportDataState approveOrReject(Boolean isApproved,
                                             SecureHash secureHash) {
        return new ExPassportDataState(owner,
                policeStation,
                fileNumber,
                pvRequestId,
                applicantName,
                gender,
                dob,
                spouseName,
                fatherName,
                pvInitiationDate,
                verificationAddress,
                permanentAddress,
                pvSequenceNo,
                emailId,
                phoneNumber,

                isAdverseByDCRB,
                dateOfDCRB,
                crimeNo,
                crimeYear,
                crimePoliceStation,
                crimeUnderSection,
                crimeRemarks,

                isAdverseByEVIP,
                dateOfFVO,
                fvo,
                fieldQuestion,
                fieldAnswer,
                fieldRemarks,

                isAdverseByFinalReport,
                finalReportDate,

                listOfListeners,
                isApproved,
                updatedBy,
                updateTimestamp,
                secureHash,
                linearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, linearId);
    }
}