package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonBuilderTest {

    @Test
    void build_withAllFields_createsPersonWithAllValues() {
        Instant now = Instant.now();
        List<Address> addresses = Arrays.asList(new Address("123 Main St", null, "City", "ST", "12345", "US"));
        List<Phone> phones = Arrays.asList(new Phone("555-1234", "MOBILE"));

        Person person = new PersonBuilder()
                .id("mongo-id")
                .externalId("ext-1")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .race("Caucasian")
                .ethnicity("Non-Hispanic")
                .updatedAt(now)
                .addresses(addresses)
                .phones(phones)
                .build();

        assertThat(person.getId()).isEqualTo("mongo-id");
        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getEmail()).isEqualTo("john@example.com");
        assertThat(person.getRace()).isEqualTo("Caucasian");
        assertThat(person.getEthnicity()).isEqualTo("Non-Hispanic");
        assertThat(person.getUpdatedAt()).isEqualTo(now);
        assertThat(person.getAddresses()).hasSize(1);
        assertThat(person.getPhones()).hasSize(1);
    }

    @Test
    void build_withMinimalFields_createsPersonWithDefaults() {
        Person person = new PersonBuilder()
                .externalId("ext-1")
                .build();

        assertThat(person.getId()).isNull();
        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isNull();
        assertThat(person.getLastName()).isNull();
        assertThat(person.getEmail()).isNull();
        assertThat(person.getRace()).isNull();
        assertThat(person.getEthnicity()).isNull();
        assertThat(person.getUpdatedAt()).isNull();
        assertThat(person.getAddresses()).isEmpty();
        assertThat(person.getPhones()).isEmpty();
    }

    @Test
    void build_withNullAddresses_usesEmptyList() {
        Person person = new PersonBuilder()
                .externalId("ext-1")
                .addresses(null)
                .build();

        assertThat(person.getAddresses()).isNotNull().isEmpty();
    }

    @Test
    void build_withNullPhones_usesEmptyList() {
        Person person = new PersonBuilder()
                .externalId("ext-1")
                .phones(null)
                .build();

        assertThat(person.getPhones()).isNotNull().isEmpty();
    }
}
