package com.example.persontransformer.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for Person objects to handle the growing number of optional fields.
 */
public class PersonBuilder {

    private String id;
    private String externalId;
    private String firstName;
    private String lastName;
    private String email;
    private String race;
    private String ethnicity;
    private Instant updatedAt;
    private List<Address> addresses = new ArrayList<>();
    private List<Phone> phones = new ArrayList<>();

    public PersonBuilder() {
    }

    public PersonBuilder id(String id) {
        this.id = id;
        return this;
    }

    public PersonBuilder externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public PersonBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder email(String email) {
        this.email = email;
        return this;
    }

    public PersonBuilder race(String race) {
        this.race = race;
        return this;
    }

    public PersonBuilder ethnicity(String ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public PersonBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PersonBuilder addresses(List<Address> addresses) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        return this;
    }

    public PersonBuilder phones(List<Phone> phones) {
        this.phones = phones != null ? phones : new ArrayList<>();
        return this;
    }

    public Person build() {
        Person person = new Person();
        person.setId(id);
        person.setExternalId(externalId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setRace(race);
        person.setEthnicity(ethnicity);
        person.setUpdatedAt(updatedAt);
        person.setAddresses(addresses);
        person.setPhones(phones);
        return person;
    }
}
