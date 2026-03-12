package com.example.persontransformer.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "persons")
public class Person {

    @Id
    private String id;

    @Indexed(unique = true)
    private String externalId;

    @Indexed
    private UUID resourceId;

    private String firstName;
    private String lastName;
    private String email;
    private String normalizedEmail;
    private Instant updatedAt;

    public Person() {
    }

    public Person(String id, String externalId, UUID resourceId, String firstName, String lastName, String email, String normalizedEmail, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.resourceId = resourceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.normalizedEmail = normalizedEmail;
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

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
