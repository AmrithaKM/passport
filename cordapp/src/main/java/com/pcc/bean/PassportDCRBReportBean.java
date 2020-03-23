package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class PassportDCRBReportBean implements Serializable {
    private String id;
	private String isAdverse;
	private String eDCRBReportDate;
	private String crimeNo;
	private String crimeYear;
	private String crimePoliceStation;
	private String crimeUnderSection;
	private String crimeRemarks;
    private String updatedBy;
    private String updateTimeStamp;

	public String getId() {
		return id;
	}

	public String getIsAdverse() {
		return isAdverse;
	}

	public String geteDCRBReportDate() {
		return eDCRBReportDate;
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

	public String getUpdatedBy() {
		return updatedBy;
	}

	public String getUpdateTimeStamp() {
		return updateTimeStamp;
	}
}
