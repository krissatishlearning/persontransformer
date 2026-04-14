package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonPreferenceTest {

    @Test
    void person_defaultConstructor_hasNullPreference() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();
    }

    @Test
    void person_setPreference_storesPreference() {
        Person person = new Person();
        Preference preference = new Preference(NotificationPreference.EMAIL, true);
        person.setPreference(preference);
        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getNotificationPreference()).isEqualTo(NotificationPreference.EMAIL);
        assertThat(person.getPreference().isNotificationEnabled()).isTrue();
    }

    @Test
    void person_fullConstructorWithPreference_storesPreference() {
        Preference preference = new Preference(NotificationPreference.TEXT, false);
        Person person = new Person(
                "id-1", "ext-1", "Jane", "Doe", "jane@example.com", null, null, null, preference
        );
        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getNotificationPreference()).isEqualTo(NotificationPreference.TEXT);
        assertThat(person.getPreference().isNotificationEnabled()).isFalse();
    }

    @Test
    void person_hasPreferenceField_viaReflection() {
        assertThat(java.util.Arrays.stream(Person.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .anyMatch(name -> name.equals("preference")))
                .isTrue();
    }

    @Test
    void person_setPreferenceToNull_isAllowed() {
        Person person = new Person();
        person.setPreference(new Preference(NotificationPreference.PHONE, true));
        person.setPreference(null);
        assertThat(person.getPreference()).isNull();
    }
}
