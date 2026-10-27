package com.example.underworld.cart_pack;

public class UpdateQuantityRequest {
    private String productId;
    private String selectedColour;
    private String selectedSize;
    private int quantity;

    public UpdateQuantityRequest(String productId, String selectedColour, String selectedSize, int quantity) {
        this.productId = productId;
        this.selectedColour = selectedColour;
        this.selectedSize = selectedSize;
        this.quantity = quantity;
    }
}
