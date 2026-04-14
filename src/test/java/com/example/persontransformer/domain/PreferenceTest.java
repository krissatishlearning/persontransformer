package com.example.persontransformer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreferenceTest {

    @Test
    void preference_defaultConstructor_notificationEnabledIsFalse() {
        Preference preference = new Preference();
        assertThat(preference.isNotificationEnabled()).isFalse();
        assertThat(preference.getNotificationPreference()).isNull();
    }

    @Test
    void preference_allArgsConstructor_setsFields() {
        Preference preference = new Preference(NotificationPreference.EMAIL, true);
        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.EMAIL);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void preference_settersAndGetters_workCorrectly() {
        Preference preference = new Preference();
        preference.setNotificationPreference(NotificationPreference.TEXT);
        preference.setNotificationEnabled(true);

        assertThat(preference.getNotificationPreference()).isEqualTo(NotificationPreference.TEXT);
        assertThat(preference.isNotificationEnabled()).isTrue();
    }

    @Test
    void notificationPreference_enumValuesExist() {
        assertThat(NotificationPreference.values()).containsExactlyInAnyOrder(
                NotificationPreference.TEXT,
                NotificationPreference.PHONE,
                NotificationPreference.EMAIL
        );
    }

    @Test
    void person_canSetAndGetPreference() {
        Person person = new Person();
        assertThat(person.getPreference()).isNull();

        Preference preference = new Preference(NotificationPreference.PHONE, false);
        person.setPreference(preference);

        assertThat(person.getPreference()).isNotNull();
        assertThat(person.getPreference().getNotificationPreference()).isEqualTo(NotificationPreference.PHONE);
        assertThat(person.getPreference().isNotificationEnabled()).isFalse();
    }

    @Test
    void person_preferenceIsNullByDefault() {
        Person person = new Person("id", "ext-1", "Jane", "Doe", "jane@example.com", null);
        assertThat(person.getPreference()).isNull();
    }

    @Test
    void preference_notificationEnabledDefaultsToFalse_withExplicitSet() {
        Preference preference = new Preference();
        preference.setNotificationPreference(NotificationPreference.EMAIL);
        // notificationEnabled not explicitly set — should default to false
        assertThat(preference.isNotificationEnabled()).isFalse();
    }
}
