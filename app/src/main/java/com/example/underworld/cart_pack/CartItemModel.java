package com.example.underworld.cart_pack;

import java.io.Serializable;

public class CartItemModel implements Serializable {

    private String _id;
    private String productId;
    private String name;
    private String category;
    private double price;
    private String currency;
    private String thumbnail;
    private CartColourModel selectedColour;
    private String selectedSize;
    private int quantity;

    public String get_id() { return _id; }
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getCurrency() { return currency; }
    public String getThumbnail() { return thumbnail; }
    public CartColourModel getSelectedColour() { return selectedColour; }
    public String getSelectedSize() { return selectedSize; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
