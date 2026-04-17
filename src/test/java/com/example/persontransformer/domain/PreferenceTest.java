package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreferenceTest {

    @Test
    void defaultConstructor_setsNotificationEnabledToFalse() {
        Preference preference = new Preference();
        assertThat(preference.isNotificationEnabled()).isFalse();
        assertThat(preference.getNotificationPreference()).isNull();
    }

    @Test
    void constructor_setsFieldsCorrectly() {
        Preference preference = new Preference(NotificationPreference.EMAIL, true);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.EMAIL);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void setters_updateFieldsCorrectly() {
        Preference preference = new Preference();
        preference.setNotificationPreference(NotificationPreference.TEXT);
        preference.setNotificationEnabled(true);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.TEXT);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void notificationPreference_allValuesExist() {
        assertThat(NotificationPreference.values()).containsExactlyInAnyOrder(
                NotificationPreference.TEXT,
                NotificationPreference.PHONE,
                NotificationPreference.EMAIL
        );
    }

    @Test
    void person_hasPreferenceField() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();

        Preference preference = new Preference(NotificationPreference.PHONE, false);
        person.setPreference(preference);

        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getNotificationPreference()).isEqualTo(NotificationPreference.PHONE);
        assertThat(person.getPreference().isNotificationEnabled()).isFalse();
    }

    @Test
    void person_preferenceFieldExistsViaReflection() {
        assertThat(java.util.Arrays.stream(Person.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .filter(name -> name.equals("preference"))
                .count()).isEqualTo(1);
    }
}
