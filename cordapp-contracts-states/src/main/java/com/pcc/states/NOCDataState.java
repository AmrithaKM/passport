package com.pcc.states;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class NOCDataState implements LinearState {
    private final Party owner;
    private final NOCApplicationDetailsState nocApplicationDetailsState;
    private final SecureHash fileAttachmentHashValue;

    private final SecureHash attachmentHashValueCertificateOfRegistration;
    private final SecureHash attachmentHashValueCertificateOfInsurance;
    private final SecureHash attachmentHashValueCertificateRCOwnerDrivingLicense;
    private final SecureHash attachmentHashValueCertificatePUC;

    private final SecureHash physicalVerificationAttachmentHashValue;
    private final SecureHash rejectionAttachmentHashValue;
    private final SecureHash finalPDFHashValue;
    private final String applicationStatus;

    private final Boolean isApproved; //Only by ACP

    private final List<AbstractParty> listOfListeners;

    //private final Boolean hasCriminalBackground; //hasCriminalBackgroundByDSBO, hasCriminalBackgroundByDCRB
    private final Boolean hasCriminalBackgroundByDSBO;
    //private final Boolean hasCriminalBackgroundByDCRB;

    private final UniqueIdentifier linearId;

    public NOCDataState(Party owner,
                        NOCApplicationDetailsState nocApplicationDetailsState,
                        SecureHash fileAttachmentHashValue,

                        SecureHash attachmentHashValueCertificateOfRegistration,
                        SecureHash attachmentHashValueCertificateOfInsurance,
                        SecureHash attachmentHashValueCertificateRCOwnerDrivingLicense,
                        SecureHash attachmentHashValueCertificatePUC,

                        List<AbstractParty> listOfListeners) {
        this.owner = owner;
        this.nocApplicationDetailsState = nocApplicationDetailsState;
        this.fileAttachmentHashValue = fileAttachmentHashValue;

        this.attachmentHashValueCertificateOfRegistration = attachmentHashValueCertificateOfRegistration;
        this.attachmentHashValueCertificateOfInsurance = attachmentHashValueCertificateOfInsurance;
        this.attachmentHashValueCertificateRCOwnerDrivingLicense = attachmentHashValueCertificateRCOwnerDrivingLicense;
        this.attachmentHashValueCertificatePUC = attachmentHashValueCertificatePUC;

        this.physicalVerificationAttachmentHashValue = null;
        this.rejectionAttachmentHashValue = null;
        this.finalPDFHashValue = null;
        this.isApproved = null;

        this.listOfListeners = listOfListeners;

        this.hasCriminalBackgroundByDSBO = null;
        //this.hasCriminalBackgroundByDCRB = null;

        this.applicationStatus = "Application Submitted";

        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public NOCDataState(Party owner,
                        NOCApplicationDetailsState nocApplicationDetailsState,
                        SecureHash fileAttachmentHashValue,

                        SecureHash attachmentHashValueCertificateOfRegistration,
                        SecureHash attachmentHashValueCertificateOfInsurance,
                        SecureHash attachmentHashValueCertificateRCOwnerDrivingLicense,
                        SecureHash attachmentHashValueCertificatePUC,

                        SecureHash physicalVerificationAttachmentHashValue,
                        SecureHash rejectionAttachmentHashValue,
                        SecureHash finalPDFHashValue,
                        List<AbstractParty> listOfListeners,
                        Boolean isApproved,
                        Boolean hasCriminalBackgroundByDSBO,
                        //Boolean hasCriminalBackgroundByDCRB,
                        String applicationStatus,
                        UniqueIdentifier linearId) {
        this.owner = owner;
        this.nocApplicationDetailsState = nocApplicationDetailsState;
        this.fileAttachmentHashValue = fileAttachmentHashValue;

        this.attachmentHashValueCertificateOfRegistration = attachmentHashValueCertificateOfRegistration;
        this.attachmentHashValueCertificateOfInsurance = attachmentHashValueCertificateOfInsurance;
        this.attachmentHashValueCertificateRCOwnerDrivingLicense = attachmentHashValueCertificateRCOwnerDrivingLicense;
        this.attachmentHashValueCertificatePUC = attachmentHashValueCertificatePUC;

        this.physicalVerificationAttachmentHashValue = physicalVerificationAttachmentHashValue;
        this.rejectionAttachmentHashValue = rejectionAttachmentHashValue;
        this.finalPDFHashValue = finalPDFHashValue;
        this.listOfListeners = listOfListeners;

        this.isApproved = isApproved;

        this.hasCriminalBackgroundByDSBO = hasCriminalBackgroundByDSBO;
        //this.hasCriminalBackgroundByDCRB = hasCriminalBackgroundByDCRB;

        this.applicationStatus = applicationStatus;

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

    public NOCApplicationDetailsState getNocApplicationDetailsState() {
        return nocApplicationDetailsState;
    }

    public SecureHash getFileAttachmentHashValue() {
        return fileAttachmentHashValue;
    }

    public SecureHash getAttachmentHashValueCertificateOfRegistration() {
        return attachmentHashValueCertificateOfRegistration;
    }

    public SecureHash getAttachmentHashValueCertificateOfInsurance() {
        return attachmentHashValueCertificateOfInsurance;
    }

    public SecureHash getAttachmentHashValueCertificateRCOwnerDrivingLicense() {
        return attachmentHashValueCertificateRCOwnerDrivingLicense;
    }

    public SecureHash getAttachmentHashValueCertificatePUC() {
        return attachmentHashValueCertificatePUC;
    }

    public SecureHash getPhysicalVerificationAttachmentHashValue() {
        return physicalVerificationAttachmentHashValue;
    }

    public SecureHash getRejectionAttachmentHashValue() {
        return rejectionAttachmentHashValue;
    }

    public SecureHash getFinalPDFHashValue() {
        return finalPDFHashValue;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public List<AbstractParty> getListOfListeners() {
        return listOfListeners;
    }

    public Boolean getHasCriminalBackgroundByDSBO() {
        return hasCriminalBackgroundByDSBO;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }


    public NOCDataState transferToAnotherRole(NOCApplicationDetailsState updatedNOCApplicationDetailsState,
                                              List<AbstractParty> updatedListOfListeners,
                                              String newApplicationStatus) {
        return new NOCDataState(owner,
                updatedNOCApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                physicalVerificationAttachmentHashValue,
                null,
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newApplicationStatus,
                linearId);
    }

    public NOCDataState transferOrganization(Party newOwner,
                                             NOCApplicationDetailsState updatedNOCApplicationDetailsState,
                                             List<AbstractParty> updatedListOfListeners,
                                             String newStatus) {

        return new NOCDataState(newOwner,
                updatedNOCApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                physicalVerificationAttachmentHashValue,
                null,
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public NOCDataState updatePhysicalVerification(NOCApplicationDetailsState updatedNOCApplicationDetailsState,
                                                   SecureHash newPhysicalVerificationAttachmentHashValue,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   String newStatus) {
        return new NOCDataState(owner,
                updatedNOCApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                newPhysicalVerificationAttachmentHashValue, //physicalVerificationAttachmentHashValue,
                null,//rejectionAttachmentHashValue
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public NOCDataState updateCriminalStatusByDSBO(Party newOwner,
                                                   NOCApplicationDetailsState updatedNOCApplicationDetailsState,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   Boolean isCriminal,
                                                   String newStatus) {
        return new NOCDataState(newOwner,
                updatedNOCApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                updatedListOfListeners,
                null, //isApproved,
                isCriminal, //hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public NOCDataState updateCriminalStatusByDCRB(NOCApplicationDetailsState updatedNOCApplicationDetailsState,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   String newStatus) {
        return new NOCDataState(owner,
                updatedNOCApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public NOCDataState approvalByACP(Boolean isApproved,
                                      NOCApplicationDetailsState updatedNOCApplicationDetailsState,
                                      SecureHash secureHash,
                                      List<AbstractParty> updatedListOfListeners,
                                      String newApplicationStatus) {
        return new NOCDataState(owner,
                updatedNOCApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                physicalVerificationAttachmentHashValue,
                secureHash,//rejectionAttachmentHashValue
                null,
                updatedListOfListeners,
                isApproved, //issuedByACP,
                hasCriminalBackgroundByDSBO,
                //hasCriminalBackgroundByDCRB,
                newApplicationStatus,
                linearId);
    }

    public NOCDataState saveCertificate(SecureHash secureHash) {
        return new NOCDataState(owner,
                nocApplicationDetailsState,
                fileAttachmentHashValue,
                attachmentHashValueCertificateOfRegistration,
                attachmentHashValueCertificateOfInsurance,
                attachmentHashValueCertificateRCOwnerDrivingLicense,
                attachmentHashValueCertificatePUC,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                secureHash,
                listOfListeners,
                isApproved,
                hasCriminalBackgroundByDSBO,
                "Certificate generated and saved to ledger",
                linearId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(owner, nocApplicationDetailsState, linearId);
    }
}