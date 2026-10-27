package com.example.underworld.order_pack;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("count")
    private int count;

    @SerializedName("nextCursor")
    private String nextCursor;

    @SerializedName("data")
    private List<OrderListItemModel> data;

    public boolean isSuccess() { return success; }
    public int getCount() { return count; }
    public String getNextCursor() { return nextCursor; }
    public List<OrderListItemModel> getData() { return data; }
}
