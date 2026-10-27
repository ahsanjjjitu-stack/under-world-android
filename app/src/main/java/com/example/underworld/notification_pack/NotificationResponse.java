package com.example.underworld.notification_pack;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationResponse {

    private boolean success;
    private int count;
    private String nextCursor;

    @SerializedName("data")
    private List<NotificationModel> data;

    public boolean isSuccess() {
        return success;
    }

    public int getCount() {
        return count;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public List<NotificationModel> getData() {
        return data;
    }

}
