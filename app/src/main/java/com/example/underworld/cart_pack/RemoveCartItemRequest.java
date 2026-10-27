package com.example.underworld.cart_pack;

public class RemoveCartItemRequest {
    private String productId;
    private String selectedColour;
    private String selectedSize;

    public RemoveCartItemRequest(String productId, String selectedColour, String selectedSize) {
        this.productId = productId;
        this.selectedColour = selectedColour;
        this.selectedSize = selectedSize;
    }
}
