package com.example.persontransformer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonEvent {

    private String externalId;
    private String firstName;
    private String lastName;
    private String email;
    private String race;
    private String ethnicity;

    public PersonEvent() {
    }

    public PersonEvent(String externalId, String firstName, String lastName, String email) {
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public PersonEvent(String externalId, String firstName, String lastName, String email, String race, String ethnicity) {
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.race = race;
        this.ethnicity = ethnicity;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }
}
