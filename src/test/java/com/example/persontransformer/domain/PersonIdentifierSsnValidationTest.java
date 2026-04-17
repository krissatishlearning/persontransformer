package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PersonIdentifierSsnValidationTest {

    // -----------------------------------------------------------------------
    // PersonIdentifier model defaults
    // -----------------------------------------------------------------------

    @Test
    void personIdentifier_defaultIsValidIsFalse() {
        PersonIdentifier identifier = new PersonIdentifier();
        assertThat(identifier.isValid()).isFalse();
    }

    @Test
    void personIdentifier_constructorSetsFields() {
        PersonIdentifier identifier = new PersonIdentifier("123-45-6789", true);
        assertThat(identifier.getValue()).isEqualTo("123-45-6789");
        assertThat(identifier.isValid()).isTrue();
    }

    @Test
    void personIdentifier_settersWork() {
        PersonIdentifier identifier = new PersonIdentifier();
        identifier.setValue("987-65-4321");
        identifier.setValid(true);
        assertThat(identifier.getValue()).isEqualTo("987-65-4321");
        assertThat(identifier.isValid()).isTrue();
    }

    // -----------------------------------------------------------------------
    // Person model has ssn field
    // -----------------------------------------------------------------------

    @Test
    void person_hasSsnField() throws NoSuchFieldException {
        assertThat(Person.class.getDeclaredField("ssn")).isNotNull();
    }

    @Test
    void person_ssnGetterAndSetterWork() {
        Person person = new Person();
        assertThat(person.getSsn()).isNull();

        PersonIdentifier ssn = new PersonIdentifier("234-56-7890", true);
        person.setSsn(ssn);
        assertThat(person.getSsn()).isSameAs(ssn);
        assertThat(person.getSsn().getValue()).isEqualTo("234-56-7890");
        assertThat(person.getSsn().isValid()).isTrue();
    }

    // -----------------------------------------------------------------------
    // Valid SSNs — positive scenarios
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "234-56-7890",  // standard dashes
            "234567890",    // no dashes
            "578-12-3456",  // random valid
            "001-01-0001",  // minimal non-zero digits
            "899-99-9999"   // upper boundary of valid area (just below 900)
    })
    void validate_validSsn_isValidTrue(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.getValue()).isEqualTo(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be valid", ssn)
                .isTrue();
    }

    // -----------------------------------------------------------------------
    // Null / blank
    // -----------------------------------------------------------------------

    @Test
    void validate_nullSsn_isValidFalse() {
        PersonIdentifier result = SsnValidator.validate(null);
        assertThat(result.getValue()).isNull();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validate_blankSsn_isValidFalse() {
        PersonIdentifier result = SsnValidator.validate("   ");
        assertThat(result.isValid()).isFalse();
    }

    // -----------------------------------------------------------------------
    // Format violations
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "123-45-678",    // too short
            "123-45-67890",  // too long
            "12-345-6789",   // wrong grouping
            "abc-de-fghi",   // non-digits
            "123 45 6789",   // spaces instead of dashes
            ""
    })
    void validate_invalidFormat_isValidFalse(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be invalid due to format", ssn)
                .isFalse();
    }

    // -----------------------------------------------------------------------
    // All-zeros in any group
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "000-45-6789",  // area all zeros
            "000456789",    // area all zeros (no dashes)
            "123-00-6789",  // group all zeros
            "123006789",    // group all zeros (no dashes)
            "123-45-0000",  // serial all zeros
            "123450000"     // serial all zeros (no dashes)
    })
    void validate_allZerosInGroup_isValidFalse(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be invalid (all zeros in a group)", ssn)
                .isFalse();
    }

    // -----------------------------------------------------------------------
    // Area 666
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "666-45-6789",
            "666456789"
    })
    void validate_area666_isValidFalse(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be invalid (area 666)", ssn)
                .isFalse();
    }

    // -----------------------------------------------------------------------
    // Area 900–999
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "900-45-6789",
            "912-34-5678",
            "999-12-3456",
            "900456789",
            "999123456"
    })
    void validate_area900to999_isValidFalse(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be invalid (area 900-999)", ssn)
                .isFalse();
    }

    // -----------------------------------------------------------------------
    // All same digit
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "111-11-1111",
            "222-22-2222",
            "333-33-3333",
            "444-44-4444",
            "555-55-5555",
            "666-66-6666",  // also caught by area 666, but tested here too
            "777-77-7777",
            "888-88-8888",
            "999-99-9999",
            "111111111",
            "999999999"
    })
    void validate_allSameDigit_isValidFalse(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be invalid (all same digit)", ssn)
                .isFalse();
    }

    // -----------------------------------------------------------------------
    // Known placeholder 123-45-6789
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "123-45-6789",
            "123456789"
    })
    void validate_knownPlaceholder_isValidFalse(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid())
                .as("Expected SSN '%s' to be invalid (known placeholder)", ssn)
                .isFalse();
    }
}
