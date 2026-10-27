package com.example.underworld.notification_pack;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {

    @SerializedName("_id")
    private String _id;
    private String title;
    private String body;
    private String type;
    private String orderId;
    private boolean isRead;
    private String createdAt;

    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public String getOrderId() {
        return orderId;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
