package com.pcc.states;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class CriminalHistoryAddressState implements Serializable {
	private String addressType;
	private String houseNo;
	private String streetName;
	private String colonyLocalArea;
	private String villageTownCity;
	private String tehsilBlockMandal;
	private String country;
	private String state;
	private String district;
	private String pinCode;
	private String policeStation;

	private String isCurrent;

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getColonyLocalArea() {
		return colonyLocalArea;
	}

	public void setColonyLocalArea(String colonyLocalArea) {
		this.colonyLocalArea = colonyLocalArea;
	}

	public String getVillageTownCity() {
		return villageTownCity;
	}

	public void setVillageTownCity(String villageTownCity) {
		this.villageTownCity = villageTownCity;
	}

	public String getTehsilBlockMandal() {
		return tehsilBlockMandal;
	}

	public void setTehsilBlockMandal(String tehsilBlockMandal) {
		this.tehsilBlockMandal = tehsilBlockMandal;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPoliceStation() {
		return policeStation;
	}

	public void setPoliceStation(String policeStation) {
		this.policeStation = policeStation;
	}

	public String getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(String isCurrent) {
		this.isCurrent = isCurrent;
	}
}