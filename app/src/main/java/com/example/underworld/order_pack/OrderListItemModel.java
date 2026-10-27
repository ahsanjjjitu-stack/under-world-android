package com.example.underworld.order_pack;

import com.google.gson.annotations.SerializedName;

public class OrderListItemModel {
    @SerializedName("_id")
    private String id;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("totalItemCount")
    private int totalItemCount;

    // Getters
    public String getId() { return id; }
    public String getOrderStatus() { return orderStatus; }
    public String getCreatedAt() { return createdAt; }
    public double getTotalAmount() { return totalAmount; }
    public int getTotalItemCount() { return totalItemCount; }
}
