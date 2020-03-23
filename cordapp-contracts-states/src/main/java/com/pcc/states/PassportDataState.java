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

public class PassportDataState implements LinearState {
    private final Party owner;
    private final PassportApplicationDetailsState passportApplicationDetailsState;
    private final SecureHash physicalVerificationAttachmentHashValue;
    private final SecureHash rejectionAttachmentHashValue;
    private final SecureHash finalPDFHashValue;
    private final String applicationStatus;

    private final Boolean isViewedByCommissioner;
    private final String commissionerNotes;
    private final Boolean isApproved; //Only by ACP

    private final List<AbstractParty> listOfListeners;

    //private final Boolean hasCriminalBackground; //hasCriminalBackgroundByDSBO, hasCriminalBackgroundByDCRB
    private final Boolean hasCriminalBackgroundByDSBO;
    //private final Boolean hasCriminalBackgroundByDCRB;

    private final UniqueIdentifier linearId;

    public PassportDataState(Party owner,
                             PassportApplicationDetailsState passportApplicationDetailsState,
                             List<AbstractParty> listOfListeners) {
        this.owner = owner;
        this.passportApplicationDetailsState = passportApplicationDetailsState;
        this.physicalVerificationAttachmentHashValue = null;
        this.rejectionAttachmentHashValue = null;
        this.finalPDFHashValue = null;
        this.isViewedByCommissioner = null;
        this.commissionerNotes = null;
        this.isApproved = null;

        this.listOfListeners = listOfListeners;

        this.hasCriminalBackgroundByDSBO = null;
        //this.hasCriminalBackgroundByDCRB = null;

        this.applicationStatus = "Application Submitted";

        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public PassportDataState(Party owner,
                             PassportApplicationDetailsState passportApplicationDetailsState,
                             SecureHash physicalVerificationAttachmentHashValue,
                             SecureHash rejectionAttachmentHashValue,
                             SecureHash finalPDFHashValue,
                             List<AbstractParty> listOfListeners,
                             Boolean isViewedByCommissioner,
                             String commissionerNotes,
                             Boolean isApproved,
                             Boolean hasCriminalBackgroundByDSBO,
                             String applicationStatus,
                             UniqueIdentifier linearId) {
        this.owner = owner;
        this.passportApplicationDetailsState = passportApplicationDetailsState;
        this.physicalVerificationAttachmentHashValue = physicalVerificationAttachmentHashValue;
        this.rejectionAttachmentHashValue = rejectionAttachmentHashValue;
        this.finalPDFHashValue = finalPDFHashValue;
        this.listOfListeners = listOfListeners;

        this.isViewedByCommissioner = isViewedByCommissioner;
        this.commissionerNotes = commissionerNotes;
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

    public PassportApplicationDetailsState getPassportApplicationDetailsState() {
        return passportApplicationDetailsState;
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

    public Boolean getViewedByCommissioner() { return isViewedByCommissioner; }

    public String getCommissionerNotes() { return commissionerNotes; }

    public Boolean getApproved() {
        return isApproved;
    }

    public List<AbstractParty> getListOfListeners() {
        return listOfListeners;
    }

    public Boolean getHasCriminalBackgroundByDSBO() {
        return hasCriminalBackgroundByDSBO;
    }

    //public Boolean getHasCriminalBackgroundByDCRB() { return hasCriminalBackgroundByDCRB; }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }


    public PassportDataState transferToAnotherRole(PassportApplicationDetailsState updatedPassportApplicationDetailsState,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   String newApplicationStatus) {
        return new PassportDataState(owner,
                updatedPassportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                null,
                null,
                updatedListOfListeners,
                null,// isViewedByCommissioner
                null,//commissionerNotes
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newApplicationStatus,
                linearId);
    }

    public PassportDataState transferOrganization(Party newOwner,
                                                  PassportApplicationDetailsState updatedPassportApplicationDetailsState,
                                                  List<AbstractParty> updatedListOfListeners,
                                                  String newStatus) {

        return new PassportDataState(newOwner,
                updatedPassportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                null,
                null,
                updatedListOfListeners,
                null,
                null,//commissionerNotes
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PassportDataState updatePhysicalVerification(PassportApplicationDetailsState updatedPassportApplicationDetailsState,
                                                        SecureHash newPhysicalVerificationAttachmentHashValue,
                                                        List<AbstractParty> updatedListOfListeners,
                                                        String newStatus) {
        return new PassportDataState(owner,
                updatedPassportApplicationDetailsState,
                newPhysicalVerificationAttachmentHashValue, //physicalVerificationAttachmentHashValue,
                null,//rejectionAttachmentHashValue
                null,
                updatedListOfListeners,
                null,
                commissionerNotes,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PassportDataState updateCriminalStatusByDSBO(Party newOwner,
                                                        PassportApplicationDetailsState updatedPassportApplicationDetailsState,
                                                        List<AbstractParty> updatedListOfListeners,
                                                        Boolean isCriminal,
                                                        String newStatus) {
        return new PassportDataState(newOwner,
                updatedPassportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                updatedListOfListeners,
                null,
                commissionerNotes,
                null, //isApproved,
                isCriminal, //hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PassportDataState updateCriminalStatusByDCRB(PassportApplicationDetailsState updatedPassportApplicationDetailsState,
                                                        List<AbstractParty> updatedListOfListeners,
                                                        String newStatus) {
        return new PassportDataState(owner,
                updatedPassportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                updatedListOfListeners,
                null,
                commissionerNotes,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PassportDataState viewedByCommissioner() {
        return new PassportDataState(owner,
                passportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                listOfListeners,
                true,
                commissionerNotes,
                null, //issuedByACP,
                hasCriminalBackgroundByDSBO,
                //hasCriminalBackgroundByDCRB,
                applicationStatus,
                linearId);
    }

    public PassportDataState updateCommissionerNotes(String newCommissionerNotes,
                                                     PassportApplicationDetailsState updatedPassportApplicationDetailsState) {
        return new PassportDataState(owner,
                updatedPassportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                listOfListeners,
                true,
                newCommissionerNotes, //commissionerNotes,
                null, //issuedByACP,
                hasCriminalBackgroundByDSBO,
                //hasCriminalBackgroundByDCRB,
                applicationStatus,
                linearId);
    }

    public PassportDataState approval(Boolean isApproved,
                                           PassportApplicationDetailsState updatedPassportApplicationDetailsState,
                                           SecureHash secureHash,
                                           List<AbstractParty> updatedListOfListeners,
                                           String newApplicationStatus) {
        return new PassportDataState(owner,
                updatedPassportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                secureHash,//rejectionAttachmentHashValue
                null,
                updatedListOfListeners,
                isViewedByCommissioner,
                commissionerNotes,
                isApproved, //issuedByACP,
                hasCriminalBackgroundByDSBO, //hasCriminalBackgroundByDCRB,
                newApplicationStatus,
                linearId);
    }

    public PassportDataState saveCertificate(SecureHash secureHash) {
        return new PassportDataState(owner,
                passportApplicationDetailsState,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                secureHash,
                listOfListeners,
                isViewedByCommissioner,
                commissionerNotes,
                isApproved,
                hasCriminalBackgroundByDSBO,
                "Certificate generated and saved to ledger",
                linearId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(owner, passportApplicationDetailsState, linearId);
    }
}