package com.example.underworld.cart_pack;

public class AddToCartRequest {
    private String productId;
    private CartColourModel selectedColour;
    private String selectedSize;
    private int quantity;

    public AddToCartRequest(String productId, CartColourModel selectedColour, String selectedSize, int quantity) {
        this.productId = productId;
        this.selectedColour = selectedColour;
        this.selectedSize = selectedSize;
        this.quantity = quantity;
    }
}
