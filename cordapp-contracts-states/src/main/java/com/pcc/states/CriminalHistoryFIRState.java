package com.pcc.states;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;
import java.util.List;

@CordaSerializable
public class CriminalHistoryFIRState implements Serializable {
	private String crimeActName;
    private String crimeSection;

    public String getCrimeActName() {
        return crimeActName;
    }

    public String getCrimeSection() {
        return crimeSection;
    }

    public void setCrimeActName(String crimeActName) {
        this.crimeActName = crimeActName;
    }

    public void setCrimeSection(String crimeSection) {
        this.crimeSection = crimeSection;
    }
}