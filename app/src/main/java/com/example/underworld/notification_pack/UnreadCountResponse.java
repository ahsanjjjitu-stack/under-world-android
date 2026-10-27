package com.example.underworld.notification_pack;

import com.google.gson.annotations.SerializedName;

public class UnreadCountResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("unreadCount")
    private int unreadCount;

    public boolean isSuccess() {
        return success;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
