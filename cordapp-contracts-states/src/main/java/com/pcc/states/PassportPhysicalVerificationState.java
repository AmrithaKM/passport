package com.pcc.states;

import net.corda.core.serialization.CordaSerializable;

import java.util.List;

@CordaSerializable

public class PassportPhysicalVerificationState {
    
    /*Physical verification flags start*/
    private String fieldDescription;
    private Boolean flagQuestion1;
    private Boolean flagQuestion2;
    private Boolean flagQuestion3;
    private Boolean flagQuestion4;
    private Boolean flagQuestion5;
    private Boolean flagQuestion6;

    private String remarkQuestion1;
    private String remarkQuestion2;
    private String remarkQuestion3;
    private String remarkQuestion4;
    private String remarkQuestion5;
    private String remarkQuestion6;
    /*Physical verification flags end*/

    public PassportPhysicalVerificationState() {

    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public Boolean getFlagQuestion1() {
        return flagQuestion1;
    }

    public void setFlagQuestion1(Boolean flagQuestion1) {
        this.flagQuestion1 = flagQuestion1;
    }

    public Boolean getFlagQuestion2() {
        return flagQuestion2;
    }

    public void setFlagQuestion2(Boolean flagQuestion2) {
        this.flagQuestion2 = flagQuestion2;
    }

    public Boolean getFlagQuestion3() {
        return flagQuestion3;
    }

    public void setFlagQuestion3(Boolean flagQuestion3) {
        this.flagQuestion3 = flagQuestion3;
    }

    public Boolean getFlagQuestion4() {
        return flagQuestion4;
    }

    public void setFlagQuestion4(Boolean flagQuestion4) {
        this.flagQuestion4 = flagQuestion4;
    }

    public Boolean getFlagQuestion5() {
        return flagQuestion5;
    }

    public void setFlagQuestion5(Boolean flagQuestion5) {
        this.flagQuestion5 = flagQuestion5;
    }

    public Boolean getFlagQuestion6() {
        return flagQuestion6;
    }

    public void setFlagQuestion6(Boolean flagQuestion6) {
        this.flagQuestion6 = flagQuestion6;
    }

    public String getRemarkQuestion1() {
        return remarkQuestion1;
    }

    public void setRemarkQuestion1(String remarkQuestion1) {
        this.remarkQuestion1 = remarkQuestion1;
    }

    public String getRemarkQuestion2() {
        return remarkQuestion2;
    }

    public void setRemarkQuestion2(String remarkQuestion2) {
        this.remarkQuestion2 = remarkQuestion2;
    }

    public String getRemarkQuestion3() {
        return remarkQuestion3;
    }

    public void setRemarkQuestion3(String remarkQuestion3) {
        this.remarkQuestion3 = remarkQuestion3;
    }

    public String getRemarkQuestion4() {
        return remarkQuestion4;
    }

    public void setRemarkQuestion4(String remarkQuestion4) {
        this.remarkQuestion4 = remarkQuestion4;
    }

    public String getRemarkQuestion5() {
        return remarkQuestion5;
    }

    public void setRemarkQuestion5(String remarkQuestion5) {
        this.remarkQuestion5 = remarkQuestion5;
    }

    public String getRemarkQuestion6() {
        return remarkQuestion6;
    }

    public void setRemarkQuestion6(String remarkQuestion6) {
        this.remarkQuestion6 = remarkQuestion6;
    }
}
