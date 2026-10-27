package com.example.underworld.order_details_pack;

import com.google.gson.annotations.SerializedName;

public class OrderDetailsResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OrderModel data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public OrderModel getData() { return data; }

}
