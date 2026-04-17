package com.example.persontransformer.validation;

import com.example.persontransformer.domain.PersonIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SsnValidatorTest {

    private final SsnValidator validator = new SsnValidator();

    // -------------------------------------------------------------------------
    // Valid SSNs
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "valid SSN with dashes: {0}")
    @ValueSource(strings = {
            "123-45-6780",
            "456-78-9012",
            "789-01-2345",
            "321-54-0987",
            "555-44-3322"
    })
    void isValid_returnsTrue_forValidSsnsWithDashes(String ssn) {
        assertThat(validator.isValid(ssn)).isTrue();
    }

    @ParameterizedTest(name = "valid SSN without dashes: {0}")
    @ValueSource(strings = {
            "123456780",
            "456789012",
            "789012345",
            "321540987",
            "555443322"
    })
    void isValid_returnsTrue_forValidSsnsWithoutDashes(String ssn) {
        assertThat(validator.isValid(ssn)).isTrue();
    }

    // -------------------------------------------------------------------------
    // Invalid format
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "invalid format: {0}")
    @ValueSource(strings = {
            "",
            "   ",
            "123-45-678",       // too short
            "123-456-7890",     // wrong grouping
            "12-345-6789",      // wrong grouping
            "abc-de-fghi",      // letters
            "123 45 6789",      // spaces instead of dashes
            "123456789012"       // too long
    })
    void isValid_returnsFalse_forInvalidFormat(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    @Test
    void isValid_returnsFalse_forNullInput() {
        assertThat(validator.isValid(null)).isFalse();
    }

    // -------------------------------------------------------------------------
    // All zeros in any group
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "area all zeros: {0}")
    @ValueSource(strings = {"000-12-3456", "000123456"})
    void isValid_returnsFalse_whenAreaIsAllZeros(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    @ParameterizedTest(name = "group all zeros: {0}")
    @ValueSource(strings = {"123-00-4567", "123004567"})
    void isValid_returnsFalse_whenGroupIsAllZeros(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    @ParameterizedTest(name = "serial all zeros: {0}")
    @ValueSource(strings = {"123-45-0000", "123450000"})
    void isValid_returnsFalse_whenSerialIsAllZeros(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    // -------------------------------------------------------------------------
    // Area 666
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "area 666: {0}")
    @ValueSource(strings = {"666-12-3456", "666123456"})
    void isValid_returnsFalse_whenAreaIs666(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    // -------------------------------------------------------------------------
    // Area 900-999 (ITINs)
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "area 900-999 (ITIN): {0}")
    @ValueSource(strings = {
            "900-12-3456",
            "950-12-3456",
            "999-12-3456",
            "900123456",
            "999123456"
    })
    void isValid_returnsFalse_whenAreaIsItin(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
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
            "666-66-6666",
            "777-77-7777",
            "888-88-8888",
            "999-99-9999",
            "111111111",
            "999999999"
    })
    void isValid_returnsFalse_whenAllSameDigit(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    // -------------------------------------------------------------------------
    // Known placeholder
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "known placeholder: {0}")
    @ValueSource(strings = {"123-45-6789", "123456789"})
    void isValid_returnsFalse_forKnownPlaceholder(String ssn) {
        assertThat(validator.isValid(ssn)).isFalse();
    }

    // -------------------------------------------------------------------------
    // validate() method returns PersonIdentifier correctly
    // -------------------------------------------------------------------------

    @Test
    void validate_returnsPersonIdentifierWithTrueForValidSsn() {
        PersonIdentifier result = validator.validate("456-78-9012");
        assertThat(result.getValue()).isEqualTo("456-78-9012");
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_returnsPersonIdentifierWithFalseForInvalidSsn() {
        PersonIdentifier result = validator.validate("000-12-3456");
        assertThat(result.getValue()).isEqualTo("000-12-3456");
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validate_returnsPersonIdentifierWithFalseForNullSsn() {
        PersonIdentifier result = validator.validate(null);
        assertThat(result.getValue()).isNull();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validate_returnsPersonIdentifierWithFalseForBlankSsn() {
        PersonIdentifier result = validator.validate("   ");
        assertThat(result.getValue()).isEqualTo("   ");
        assertThat(result.isValid()).isFalse();
    }
}
