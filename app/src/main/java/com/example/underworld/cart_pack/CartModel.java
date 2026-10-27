package com.example.underworld.cart_pack;

import java.util.List;

public class CartModel {
    private String _id;
    private String userId;
    private List<CartItemModel> items;
    private double totalAmount;

    private int currentPage;
    private int totalPages;

    public String get_id() { return _id; }
    public String getUserId() { return userId; }
    public List<CartItemModel> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
