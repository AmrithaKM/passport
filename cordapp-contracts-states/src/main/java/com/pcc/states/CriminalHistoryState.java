package com.pcc.states;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;
import java.util.List;

@CordaSerializable
public class CriminalHistoryState implements Serializable {
    private String firstName;
	private String middleName;
	private String lastName;
	private String relativeName;

	private List<?> listCriminalHistoryAddressState;
	private List<?> listCriminalHistoryFIRState;

	private String fir_reg_num;
	private String fir_district_cd;
	private String fir_ps_cd;
	private String firDisplay;
	private String accused_srno;
	private String address_srno;

	private String isSelected;

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

	public String getRelativeName() {
		return relativeName;
	}

	public void setRelativeName(String relativeName) {
		this.relativeName = relativeName;
	}

	public String getFir_reg_num() {
		return fir_reg_num;
	}

	public void setFir_reg_num(String fir_reg_num) {
		this.fir_reg_num = fir_reg_num;
	}

	public String getFir_district_cd() {
		return fir_district_cd;
	}

	public void setFir_district_cd(String fir_district_cd) {
		this.fir_district_cd = fir_district_cd;
	}

	public String getFir_ps_cd() {
		return fir_ps_cd;
	}

	public void setFir_ps_cd(String fir_ps_cd) {
		this.fir_ps_cd = fir_ps_cd;
	}

	public String getFirDisplay() {
		return firDisplay;
	}

	public void setFirDisplay(String firDisplay) {
		this.firDisplay = firDisplay;
	}

	public String getAccused_srno() {
		return accused_srno;
	}

	public void setAccused_srno(String accused_srno) {
		this.accused_srno = accused_srno;
	}

	public String getAddress_srno() {
		return address_srno;
	}

	public void setAddress_srno(String address_srno) {
		this.address_srno = address_srno;
	}

	public List<?> getListCriminalHistoryAddressState() {
		return listCriminalHistoryAddressState;
	}

	public void setListCriminalHistoryAddressState(List<?> listCriminalHistoryAddressState) {
		this.listCriminalHistoryAddressState = listCriminalHistoryAddressState;
	}

	public List<?> getListCriminalHistoryFIRState() {
		return listCriminalHistoryFIRState;
	}

	public void setListCriminalHistoryFIRState(List<?> listCriminalHistoryFIRState) {
		this.listCriminalHistoryFIRState = listCriminalHistoryFIRState;
	}

	public String getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(String isSelected) {
		this.isSelected = isSelected;
	}
}