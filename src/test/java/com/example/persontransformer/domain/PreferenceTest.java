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
    void fullConstructor_setsAllFields() {
        Preference preference = new Preference(NotificationPreference.EMAIL, true);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.EMAIL);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void setters_updateFields() {
        Preference preference = new Preference();
        preference.setNotificationPreference(NotificationPreference.TEXT);
        preference.setNotificationEnabled(true);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.TEXT);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void notificationPreference_allEnumValues_areAvailable() {
        assertThat(NotificationPreference.values()).containsExactlyInAnyOrder(
                NotificationPreference.TEXT,
                NotificationPreference.PHONE,
                NotificationPreference.EMAIL
        );
    }

    @Test
    void person_preferenceFieldExists_andIsNullByDefault() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();
    }

    @Test
    void person_setPreference_storesPreference() {
        Person person = new Person();
        Preference preference = new Preference(NotificationPreference.PHONE, false);
        person.setPreference(preference);
        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getNotificationPreference()).isEqualTo(NotificationPreference.PHONE);
        assertThat(person.getPreference().isNotificationEnabled()).isFalse();
    }
}
