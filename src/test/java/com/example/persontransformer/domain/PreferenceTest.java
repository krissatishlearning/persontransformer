package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreferenceTest {

    @Test
    void defaultConstructor_setsOptInToFalse() {
        Preference preference = new Preference();
        assertThat(preference.getOptIn()).isFalse();
        assertThat(preference.getPreferredContactNumber()).isNull();
        assertThat(preference.getPreferredEmail()).isNull();
    }

    @Test
    void parameterizedConstructor_setsAllFields() {
        Preference preference = new Preference("555-1234", "test@example.com", true);
        assertThat(preference.getPreferredContactNumber()).isEqualTo("555-1234");
        assertThat(preference.getPreferredEmail()).isEqualTo("test@example.com");
        assertThat(preference.getOptIn()).isTrue();
    }

    @Test
    void parameterizedConstructor_withNullOptIn_defaultsToFalse() {
        Preference preference = new Preference(null, null, null);
        assertThat(preference.getOptIn()).isFalse();
    }

    @Test
    void setters_updateFields() {
        Preference preference = new Preference();
        preference.setPreferredContactNumber("555-9999");
        preference.setPreferredEmail("pref@example.com");
        preference.setOptIn(true);

        assertThat(preference.getPreferredContactNumber()).isEqualTo("555-9999");
        assertThat(preference.getPreferredEmail()).isEqualTo("pref@example.com");
        assertThat(preference.getOptIn()).isTrue();
    }

    @Test
    void nullableFields_acceptNull() {
        Preference preference = new Preference();
        preference.setPreferredContactNumber(null);
        preference.setPreferredEmail(null);
        assertThat(preference.getPreferredContactNumber()).isNull();
        assertThat(preference.getPreferredEmail()).isNull();
    }
}
