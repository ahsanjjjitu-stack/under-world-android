package com.example.underworld.order_details_pack;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel {

    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("items")
    private List<OrderItemModel> items;

    @SerializedName("shippingAddress")
    private ShippingAddressModel shippingAddress;

    @SerializedName("payment")
    private PaymentModel payment;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("createdAt")
    private String createdAt;

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public List<OrderItemModel> getItems() { return items; }
    public ShippingAddressModel getShippingAddress() { return shippingAddress; }
    public PaymentModel getPayment() { return payment; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getCreatedAt() { return createdAt; }

}
