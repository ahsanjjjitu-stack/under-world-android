package com.example.underworld.profile_pack;

import com.google.gson.annotations.SerializedName;

public class NotificationSettingResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("pushNotificationsEnabled")
    private boolean pushNotificationsEnabled;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPushNotificationsEnabled() {
        return pushNotificationsEnabled;
    }
}
