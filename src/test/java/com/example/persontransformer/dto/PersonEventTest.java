package com.example.persontransformer.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonEventTest {

    @Test
    void defaultConstructor_createsEmptyInstance() {
        PersonEvent event = new PersonEvent();
        assertThat(event.getExternalId()).isNull();
        assertThat(event.getFirstName()).isNull();
        assertThat(event.getLastName()).isNull();
        assertThat(event.getEmail()).isNull();
    }

    @Test
    void parameterizedConstructor_setsAllFields() {
        PersonEvent event = new PersonEvent("ext-1", "John", "Doe", "john@example.com");

        assertThat(event.getExternalId()).isEqualTo("ext-1");
        assertThat(event.getFirstName()).isEqualTo("John");
        assertThat(event.getLastName()).isEqualTo("Doe");
        assertThat(event.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void settersAndGetters_workCorrectly() {
        PersonEvent event = new PersonEvent();

        event.setExternalId("ext-2");
        event.setFirstName("Jane");
        event.setLastName("Smith");
        event.setEmail("jane@example.com");

        assertThat(event.getExternalId()).isEqualTo("ext-2");
        assertThat(event.getFirstName()).isEqualTo("Jane");
        assertThat(event.getLastName()).isEqualTo("Smith");
        assertThat(event.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void setters_allowNullValues() {
        PersonEvent event = new PersonEvent("ext-1", "John", "Doe", "john@example.com");

        event.setExternalId(null);
        event.setFirstName(null);
        event.setLastName(null);
        event.setEmail(null);

        assertThat(event.getExternalId()).isNull();
        assertThat(event.getFirstName()).isNull();
        assertThat(event.getLastName()).isNull();
        assertThat(event.getEmail()).isNull();
    }
}
