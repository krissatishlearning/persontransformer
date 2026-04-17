package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SsnValidatorTest {

    // -------------------------------------------------------------------------
    // Valid SSNs
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "valid SSN with dashes: {0}")
    @ValueSource(strings = {
            "123-45-6780",   // not 123-45-6789
            "001-01-0001",
            "521-07-4000",
            "765-43-2100",
            "899-99-9999"
    })
    void validate_validSsnWithDashes_isValid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.getValue()).isEqualTo(ssn);
        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest(name = "valid SSN without dashes: {0}")
    @ValueSource(strings = {
            "123456780",
            "001010001",
            "521074000",
            "765432100"
    })
    void validate_validSsnWithoutDashes_isValid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isTrue();
    }

    // -------------------------------------------------------------------------
    // Null / blank / bad format
    // -------------------------------------------------------------------------

    @Test
    void validate_nullSsn_isInvalid() {
        PersonIdentifier result = SsnValidator.validate(null);
        assertThat(result.getValue()).isNull();
        assertThat(result.isValid()).isFalse();
    }

    @ParameterizedTest(name = "invalid format: ''{0}''")
    @ValueSource(strings = {
            "",
            "   ",
            "123-45-678",      // too short
            "123-45-67890",    // too long
            "12-345-6789",     // wrong grouping
            "123456789 ",      // trailing space handled by trim — but let's also test bad chars
            "abc-de-fghi",
            "123 45 6789",     // spaces instead of dashes
            "123-4A-6789"
    })
    void validate_invalidFormat_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Area group = 000
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "area 000: {0}")
    @ValueSource(strings = {"000-12-3456", "000123456"})
    void validate_areaZeros_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Area group = 666
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "area 666: {0}")
    @ValueSource(strings = {"666-12-3456", "666123456"})
    void validate_area666_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Area group 900–999 (ITINs)
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "area {0} reserved")
    @ValueSource(strings = {
            "900-12-3456",
            "950-12-3456",
            "999-12-3456",
            "901123456"
    })
    void validate_areaItin_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Group digits 00
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "group 00: {0}")
    @ValueSource(strings = {"123-00-6789", "123006789"})
    void validate_groupZeros_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Serial digits 0000
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "serial 0000: {0}")
    @ValueSource(strings = {"123-45-0000", "123450000"})
    void validate_serialZeros_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // All same digit
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "all same digit: {0}")
    @ValueSource(strings = {
            "111-11-1111",
            "222-22-2222",
            "333-33-3333",
            "444-44-4444",
            "555-55-5555",
            "777-77-7777",
            "888-88-8888",
            "999-99-9999",
            "111111111",
            "999999999"
    })
    void validate_allSameDigit_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // Note: 000-00-0000 and 666-66-6666 are caught by area-zero / area-666 rules
    // before reaching the all-same-digit check, but are invalid regardless.
    @Test
    void validate_allZeros_isInvalid() {
        assertThat(SsnValidator.validate("000-00-0000").isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Known test/placeholder 123-45-6789
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "placeholder: {0}")
    @ValueSource(strings = {"123-45-6789", "123456789"})
    void validate_knownPlaceholder_isInvalid(String ssn) {
        PersonIdentifier result = SsnValidator.validate(ssn);
        assertThat(result.isValid()).isFalse();
    }

    // -------------------------------------------------------------------------
    // PersonIdentifier default isValid
    // -------------------------------------------------------------------------

    @Test
    void personIdentifier_defaultIsValid_isFalse() {
        PersonIdentifier identifier = new PersonIdentifier();
        assertThat(identifier.isValid()).isFalse();
    }

    @Test
    void personIdentifier_constructorSetsFields() {
        PersonIdentifier identifier = new PersonIdentifier("123-45-6780", true);
        assertThat(identifier.getValue()).isEqualTo("123-45-6780");
        assertThat(identifier.isValid()).isTrue();
    }

    // -------------------------------------------------------------------------
    // Person model has ssn field
    // -------------------------------------------------------------------------

    @Test
    void person_hasSsnField() throws NoSuchFieldException {
        assertThat(Person.class.getDeclaredField("ssn")).isNotNull();
        assertThat(Person.class.getDeclaredField("ssn").getType()).isEqualTo(PersonIdentifier.class);
    }

    @Test
    void person_ssnGetterAndSetter() {
        Person person = new Person();
        assertThat(person.getSsn()).isNull();

        PersonIdentifier ssn = new PersonIdentifier("521-07-4000", true);
        person.setSsn(ssn);
        assertThat(person.getSsn()).isSameAs(ssn);
        assertThat(person.getSsn().getValue()).isEqualTo("521-07-4000");
        assertThat(person.getSsn().isValid()).isTrue();
    }

    // -------------------------------------------------------------------------
    // Whitespace trimming
    // -------------------------------------------------------------------------

    @Test
    void validate_ssnWithLeadingTrailingWhitespace_trimmedAndValidated() {
        PersonIdentifier result = SsnValidator.validate("  521-07-4000  ");
        assertThat(result.getValue()).isEqualTo("521-07-4000");
        assertThat(result.isValid()).isTrue();
    }
}
