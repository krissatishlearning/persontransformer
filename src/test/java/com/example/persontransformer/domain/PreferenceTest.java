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
    void allArgsConstructor_setsFieldsCorrectly() {
        Preference preference = new Preference(NotificationPreference.EMAIL, true);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.EMAIL);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Preference preference = new Preference();
        preference.setNotificationPreference(NotificationPreference.TEXT);
        preference.setNotificationEnabled(true);

        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.TEXT);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void notificationPreference_hasTextPhoneEmailValues() {
        NotificationPreference[] values = NotificationPreference.values();
        assertThat(values).containsExactlyInAnyOrder(
                NotificationPreference.TEXT,
                NotificationPreference.PHONE,
                NotificationPreference.EMAIL
        );
    }

    @Test
    void person_hasPreferenceField() {
        assertThat(java.util.Arrays.stream(Person.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .filter(name -> name.equals("preference"))
                .count()).isEqualTo(1);
    }

    @Test
    void person_preferenceDefaultsToNull() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();
    }

    @Test
    void person_setPreference_storesAndReturnsPreference() {
        Person person = new Person();
        Preference preference = new Preference(NotificationPreference.PHONE, false);
        person.setPreference(preference);

        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getNotificationPreference()).isEqualTo(NotificationPreference.PHONE);
        assertThat(person.getPreference().isNotificationEnabled()).isFalse();
    }

    @Test
    void preference_notificationEnabledCanBeSetToTrue() {
        Preference preference = new Preference();
        preference.setNotificationEnabled(true);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }
}
