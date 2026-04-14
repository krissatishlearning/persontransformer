package com.example.persontransformer.domain;

public class Preference {

    private NotificationPreference notificationPreference;
    private boolean notificationEnabled;

    public Preference() {
        this.notificationEnabled = false;
    }

    public Preference(NotificationPreference notificationPreference, boolean notificationEnabled) {
        this.notificationPreference = notificationPreference;
        this.notificationEnabled = notificationEnabled;
    }

    public NotificationPreference getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(NotificationPreference notificationPreference) {
        this.notificationPreference = notificationPreference;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
}
