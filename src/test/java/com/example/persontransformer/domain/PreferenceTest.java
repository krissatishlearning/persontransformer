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
    void allArgsConstructor_setsAllFields() {
        Preference preference = new Preference("555-1234", "test@example.com", true);
        assertThat(preference.getPreferredContactNumber()).isEqualTo("555-1234");
        assertThat(preference.getPreferredEmail()).isEqualTo("test@example.com");
        assertThat(preference.getOptIn()).isTrue();
    }

    @Test
    void allArgsConstructor_withNullOptIn_defaultsToFalse() {
        Preference preference = new Preference(null, null, null);
        assertThat(preference.getOptIn()).isFalse();
        assertThat(preference.getPreferredContactNumber()).isNull();
        assertThat(preference.getPreferredEmail()).isNull();
    }

    @Test
    void allArgsConstructor_withNullContactFields_acceptsNulls() {
        Preference preference = new Preference(null, null, false);
        assertThat(preference.getPreferredContactNumber()).isNull();
        assertThat(preference.getPreferredEmail()).isNull();
        assertThat(preference.getOptIn()).isFalse();
    }

    @Test
    void setters_updateFieldsCorrectly() {
        Preference preference = new Preference();
        preference.setPreferredContactNumber("555-9999");
        preference.setPreferredEmail("updated@example.com");
        preference.setOptIn(true);

        assertThat(preference.getPreferredContactNumber()).isEqualTo("555-9999");
        assertThat(preference.getPreferredEmail()).isEqualTo("updated@example.com");
        assertThat(preference.getOptIn()).isTrue();
    }

    @Test
    void person_canHavePreferenceSet() {
        Person person = new Person("id", "ext-1", "Jane", "Doe", "jane@example.com", null);
        assertThat(person.getPreference()).isNull();

        Preference preference = new Preference("555-0000", "jane@example.com", true);
        person.setPreference(preference);

        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getPreferredContactNumber()).isEqualTo("555-0000");
        assertThat(person.getPreference().getPreferredEmail()).isEqualTo("jane@example.com");
        assertThat(person.getPreference().getOptIn()).isTrue();
    }

    @Test
    void person_preferenceDefaultsToNull() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();
    }
}
