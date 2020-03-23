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

public class PCCDataState implements LinearState {
    private final Party owner;
    private final PCCApplicationDetailsState pccApplicationDetailsState;
    private final SecureHash addressProofAttachmentHashValue;
    private final SecureHash identityProofAttachmentHashValue;
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

    public PCCDataState(Party owner,
                        PCCApplicationDetailsState pccApplicationDetailsState,
                        SecureHash addressProofAttachmentHashValue,
                        SecureHash identityProofAttachmentHashValue,
                        List<AbstractParty> listOfListeners) {
        this.owner = owner;
        this.pccApplicationDetailsState = pccApplicationDetailsState;
        this.addressProofAttachmentHashValue = addressProofAttachmentHashValue;
        this.identityProofAttachmentHashValue = identityProofAttachmentHashValue;
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
    public PCCDataState(Party owner,
                        PCCApplicationDetailsState pccApplicationDetailsState,
                        SecureHash addressProofAttachmentHashValue,
                        SecureHash identityProofAttachmentHashValue,
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
        this.pccApplicationDetailsState = pccApplicationDetailsState;
        this.addressProofAttachmentHashValue = addressProofAttachmentHashValue;
        this.identityProofAttachmentHashValue = identityProofAttachmentHashValue;
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

    public PCCApplicationDetailsState getPccApplicationDetailsState() {
        return pccApplicationDetailsState;
    }

    public SecureHash getAddressProofAttachmentHashValue() {
        return addressProofAttachmentHashValue;
    }

    public SecureHash getIdentityProofAttachmentHashValue() {
        return identityProofAttachmentHashValue;
    }

    public SecureHash getPhysicalVerificationAttachmentHashValue() {
        return physicalVerificationAttachmentHashValue;
    }

    public SecureHash getRejectionAttachmentHashValue() {
        return rejectionAttachmentHashValue;
    }

    public SecureHash getFinalPDFHashValue() { return finalPDFHashValue; }

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

    //public Boolean getHasCriminalBackgroundByDCRB() { return hasCriminalBackgroundByDCRB; }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }



    public PCCDataState transferToAnotherRole(PCCApplicationDetailsState updatedPCCApplicationDetailsState,
                                              List<AbstractParty> updatedListOfListeners,
                                              String newApplicationStatus) {
        return new PCCDataState(owner,
                updatedPCCApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
                physicalVerificationAttachmentHashValue,
                null,
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newApplicationStatus,
                linearId);
    }

    public PCCDataState transferOrganization(Party newOwner,
                                             PCCApplicationDetailsState updatedPCCApplicationDetailsState,
                                             List<AbstractParty> updatedListOfListeners,
                                             String newStatus) {

        return new PCCDataState(newOwner,
                updatedPCCApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
                physicalVerificationAttachmentHashValue,
                null,
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PCCDataState updatePhysicalVerification(PCCApplicationDetailsState updatedPCCApplicationDetailsState,
                                                   SecureHash newPhysicalVerificationAttachmentHashValue,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   String newStatus) {
        return new PCCDataState(owner,
                updatedPCCApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
                newPhysicalVerificationAttachmentHashValue, //physicalVerificationAttachmentHashValue,
                null,//rejectionAttachmentHashValue
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PCCDataState updateCriminalStatusByDSBO(Party newOwner,
                                                   PCCApplicationDetailsState updatedPCCApplicationDetailsState,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   Boolean isCriminal,
                                                   String newStatus) {
        return new PCCDataState(newOwner,
                updatedPCCApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                updatedListOfListeners,
                null, //isApproved,
                isCriminal, //hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PCCDataState updateCriminalStatusByDCRB(PCCApplicationDetailsState updatedPCCApplicationDetailsState,
                                                   List<AbstractParty> updatedListOfListeners,
                                                   String newStatus) {
        return new PCCDataState(owner,
                updatedPCCApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
                physicalVerificationAttachmentHashValue,
                rejectionAttachmentHashValue,
                null,
                updatedListOfListeners,
                null, //isApproved,
                hasCriminalBackgroundByDSBO,
                newStatus,
                linearId);
    }

    public PCCDataState approvalByACP(Boolean isApproved,
                                      PCCApplicationDetailsState updatedPCCApplicationDetailsState,
                                      SecureHash secureHash,
                                      List<AbstractParty> updatedListOfListeners,
                                      String newApplicationStatus) {
        return new PCCDataState(owner,
                updatedPCCApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
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

    public PCCDataState saveCertificate(SecureHash secureHash) {
        return new PCCDataState(owner,
                pccApplicationDetailsState,
                addressProofAttachmentHashValue,
                identityProofAttachmentHashValue,
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
        return Objects.hash(owner, pccApplicationDetailsState, linearId);
    }
}