package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonPreferenceTest {

    @Test
    void person_hasPreferenceField() {
        assertThat(java.util.Arrays.stream(Person.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .filter(name -> name.equals("preference"))
                .count()).isEqualTo(1);
    }

    @Test
    void person_preferenceIsNullByDefault() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();
    }

    @Test
    void person_setAndGetPreference() {
        Person person = new Person();
        Preference preference = new Preference("555-0000", "contact@example.com", true);
        person.setPreference(preference);

        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getPreferredContactNumber()).isEqualTo("555-0000");
        assertThat(person.getPreference().getPreferredEmail()).isEqualTo("contact@example.com");
        assertThat(person.getPreference().getOptIn()).isTrue();
    }

    @Test
    void person_setPreferenceToNull_isAllowed() {
        Person person = new Person();
        Preference preference = new Preference();
        person.setPreference(preference);
        person.setPreference(null);
        assertThat(person.getPreference()).isNull();
    }
}
