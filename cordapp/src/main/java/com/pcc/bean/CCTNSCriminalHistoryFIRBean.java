package com.pcc.bean;

import net.corda.core.serialization.CordaSerializable;

import java.io.Serializable;

@CordaSerializable
public class CCTNSCriminalHistoryFIRBean implements Serializable {
    private String crimeActName;
    private String crimeSection;


    public String getCrimeActName() {
        return crimeActName;
    }

    public String getCrimeSection() {
        return crimeSection;
    }
}
