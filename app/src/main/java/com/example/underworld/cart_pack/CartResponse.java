package com.example.underworld.cart_pack;

public class CartResponse {
    private boolean success;
    private String message;
    private CartModel data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public CartModel getData() { return data; }
}
