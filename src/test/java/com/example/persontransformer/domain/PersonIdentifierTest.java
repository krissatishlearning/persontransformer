package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonIdentifierTest {

    @Test
    void defaultConstructor_setsIsValidToFalse() {
        PersonIdentifier identifier = new PersonIdentifier();
        assertThat(identifier.isValid()).isFalse();
        assertThat(identifier.getValue()).isNull();
    }

    @Test
    void constructorWithArgs_setsFieldsCorrectly() {
        PersonIdentifier identifier = new PersonIdentifier("123-45-6780", true);
        assertThat(identifier.getValue()).isEqualTo("123-45-6780");
        assertThat(identifier.isValid()).isTrue();
    }

    @Test
    void setters_updateFieldsCorrectly() {
        PersonIdentifier identifier = new PersonIdentifier();
        identifier.setValue("987-65-4321");
        identifier.setValid(true);
        assertThat(identifier.getValue()).isEqualTo("987-65-4321");
        assertThat(identifier.isValid()).isTrue();
    }

    @Test
    void isValid_defaultsToFalse_whenCreatedWithNoArgs() {
        PersonIdentifier identifier = new PersonIdentifier();
        assertThat(identifier.isValid()).isFalse();
    }

    @Test
    void personHasSsnField() {
        Person person = new Person();
        assertThat(person.getSsn()).isNull();

        PersonIdentifier ssn = new PersonIdentifier("123-45-6780", true);
        person.setSsn(ssn);
        assertThat(person.getSsn()).isNotNull();
        assertThat(person.getSsn().getValue()).isEqualTo("123-45-6780");
        assertThat(person.getSsn().isValid()).isTrue();
    }
}
