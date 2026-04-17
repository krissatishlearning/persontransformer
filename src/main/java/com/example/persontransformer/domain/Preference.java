package com.example.persontransformer.domain;

public class Preference {

    private String preferredContactNumber;
    private String preferredEmail;
    private Boolean optIn;

    public Preference() {
        this.optIn = false;
    }

    public Preference(String preferredContactNumber, String preferredEmail, Boolean optIn) {
        this.preferredContactNumber = preferredContactNumber;
        this.preferredEmail = preferredEmail;
        this.optIn = optIn != null ? optIn : false;
    }

    public String getPreferredContactNumber() {
        return preferredContactNumber;
    }

    public void setPreferredContactNumber(String preferredContactNumber) {
        this.preferredContactNumber = preferredContactNumber;
    }

    public String getPreferredEmail() {
        return preferredEmail;
    }

    public void setPreferredEmail(String preferredEmail) {
        this.preferredEmail = preferredEmail;
    }

    public Boolean getOptIn() {
        return optIn;
    }

    public void setOptIn(Boolean optIn) {
        this.optIn = optIn;
    }
}
