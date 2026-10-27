package com.example.underworld.order_details_pack;

import com.example.underworld.home_pack.ColourModel;
import com.google.gson.annotations.SerializedName;

public class OrderItemModel {

    @SerializedName("_id")
    private String id;

    @SerializedName("productId")
    private String productId;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("selectedColour")
    private ColourModelOrder selectedColour;

    @SerializedName("selectedSize")
    private String selectedSize;

    // Getters
    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public ColourModelOrder getSelectedColour() {
        return selectedColour;
    }

    public String getSelectedSize() { return selectedSize; }

}
