package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class CriminalHistoryBean implements Serializable {
    private String firstName;
	private String lastName; 
	private String relativeName; 
	private String crimeDetails;
	private String crimeLaw; 
	private String crimeSection; 
	private String crimeJurisdiction ;
	private String isSelected;

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getRelativeName() {
		return relativeName;
	}

	public String getCrimeDetails() {
		return crimeDetails;
	}

	public String getCrimeLaw() {
		return crimeLaw;
	}

	public String getCrimeSection() {
		return crimeSection;
	}

	public String getCrimeJurisdiction() {
		return crimeJurisdiction;
	}

	public String getIsSelected() {
		return isSelected;
	}
}
