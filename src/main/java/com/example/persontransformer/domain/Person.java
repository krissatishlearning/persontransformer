package com.example.persontransformer.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "persons")
public class Person {

    @Id
    private String id;

    @Indexed(unique = true)
    private String externalId;

    private String firstName;
    private String lastName;
    private String email;
    private Instant updatedAt;
    private List<Address> addresses;
    private List<Phone> phones;
    private Preference preference;

    public Person() {
        this.addresses = new ArrayList<>();
        this.phones = new ArrayList<>();
    }

    public Person(String id, String externalId, String firstName, String lastName, String email, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = updatedAt;
        this.addresses = new ArrayList<>();
        this.phones = new ArrayList<>();
    }

    public Person(String id, String externalId, String firstName, String lastName, String email, Instant updatedAt, List<Address> addresses, List<Phone> phones) {
        this.id = id;
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = updatedAt;
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        this.phones = phones != null ? phones : new ArrayList<>();
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }
}
