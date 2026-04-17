package com.example.persontransformer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreferenceDTO {

    private String preferredContactNumber;
    private String preferredEmail;
    private Boolean optIn;

    public PreferenceDTO() {
    }

    public PreferenceDTO(String preferredContactNumber, String preferredEmail, Boolean optIn) {
        this.preferredContactNumber = preferredContactNumber;
        this.preferredEmail = preferredEmail;
        this.optIn = optIn;
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
