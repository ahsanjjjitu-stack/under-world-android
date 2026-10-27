package com.example.underworld.profile_pack;

import com.google.gson.annotations.SerializedName;

public class NotificationSettingRequest {
    @SerializedName("pushNotificationsEnabled")
    private boolean pushNotificationsEnabled;

    public NotificationSettingRequest(boolean pushNotificationsEnabled) {
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }

    public boolean isPushNotificationsEnabled() {
        return pushNotificationsEnabled;
    }

    public void setPushNotificationsEnabled(boolean pushNotificationsEnabled) {
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }

}
