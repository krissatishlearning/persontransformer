package com.example.persontransformer.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "persons")
public class Person {

    @Id
    private String id;

    @Indexed(unique = true)
    private String externalId;

    private String firstName;
    private String lastName;
    private String email;
    private String normalizedEmail;
    private String race;
    private String ethnicity;
    private Instant updatedAt;

    public Person() {
    }

    public Person(String id, String externalId, String firstName, String lastName, String email, String normalizedEmail, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
        this.updatedAt = updatedAt;
    }

    public Person(String id, String externalId, String firstName, String lastName, String email, String normalizedEmail, String race, String ethnicity, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
        this.race = race;
        this.ethnicity = ethnicity;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNormalizedEmail() {
        return normalizedEmail;
    }

    public void setNormalizedEmail(String normalizedEmail) {
        this.normalizedEmail = normalizedEmail;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
