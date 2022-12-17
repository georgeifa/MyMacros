package com.example.mymacros.Domains;

public class Settings {

    public static final String ACTION_BIOMETRIC_ENROLL = "";
    public static final String EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED = "";
    private String language;
    private boolean notifications;
    private boolean app_lock;

    public Settings() {
        this.language = "english";
        this.notifications = true;
        this.app_lock = false;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public boolean isApp_lock() {
        return app_lock;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public void setApp_lock(boolean app_lock) {
        this.app_lock = app_lock;
    }
}
