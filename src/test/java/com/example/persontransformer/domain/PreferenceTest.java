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
    void parameterizedConstructor_setsFieldsCorrectly() {
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
    void notificationPreference_allEnumValues_areAvailable() {
        assertThat(NotificationPreference.values()).containsExactlyInAnyOrder(
                NotificationPreference.TEXT,
                NotificationPreference.PHONE,
                NotificationPreference.EMAIL
        );
    }

    @Test
    void notificationPreference_phoneValue_isCorrect() {
        Preference preference = new Preference(NotificationPreference.PHONE, false);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.PHONE);
        assertThat(preference.isNotificationEnabled()).isFalse();
    }
}
