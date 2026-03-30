package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    @Test
    void defaultConstructor_createsEmptyInstance() {
        Person person = new Person();
        assertThat(person.getId()).isNull();
        assertThat(person.getExternalId()).isNull();
        assertThat(person.getFirstName()).isNull();
        assertThat(person.getLastName()).isNull();
        assertThat(person.getEmail()).isNull();
        assertThat(person.getNormalizedEmail()).isNull();
        assertThat(person.getUpdatedAt()).isNull();
    }

    @Test
    void parameterizedConstructor_setsAllFields() {
        Instant now = Instant.now();
        Person person = new Person("id-1", "ext-1", "John", "Doe", "John@Example.COM", "john@example.com", now);

        assertThat(person.getId()).isEqualTo("id-1");
        assertThat(person.getExternalId()).isEqualTo("ext-1");
        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getEmail()).isEqualTo("John@Example.COM");
        assertThat(person.getNormalizedEmail()).isEqualTo("john@example.com");
        assertThat(person.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Person person = new Person();
        Instant now = Instant.now();

        person.setId("id-2");
        person.setExternalId("ext-2");
        person.setFirstName("Jane");
        person.setLastName("Smith");
        person.setEmail("jane@example.com");
        person.setNormalizedEmail("jane@example.com");
        person.setUpdatedAt(now);

        assertThat(person.getId()).isEqualTo("id-2");
        assertThat(person.getExternalId()).isEqualTo("ext-2");
        assertThat(person.getFirstName()).isEqualTo("Jane");
        assertThat(person.getLastName()).isEqualTo("Smith");
        assertThat(person.getEmail()).isEqualTo("jane@example.com");
        assertThat(person.getNormalizedEmail()).isEqualTo("jane@example.com");
        assertThat(person.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void setters_allowNullValues() {
        Instant now = Instant.now();
        Person person = new Person("id-1", "ext-1", "John", "Doe", "john@example.com", "john@example.com", now);

        person.setId(null);
        person.setExternalId(null);
        person.setFirstName(null);
        person.setLastName(null);
        person.setEmail(null);
        person.setNormalizedEmail(null);
        person.setUpdatedAt(null);

        assertThat(person.getId()).isNull();
        assertThat(person.getExternalId()).isNull();
        assertThat(person.getFirstName()).isNull();
        assertThat(person.getLastName()).isNull();
        assertThat(person.getEmail()).isNull();
        assertThat(person.getNormalizedEmail()).isNull();
        assertThat(person.getUpdatedAt()).isNull();
    }
}
