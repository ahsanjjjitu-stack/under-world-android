package com.example.underworld.home_pack;

import java.util.List;

public class ProductModel {
    private String _id;
    private String id;
    private String name;
    private String category;
    private double price;
    private String currency;
    private String description;
    private String thumbnail;
    private List<ColourModel> colours;
    private boolean isActive;

    public String get_id() {
        return _id;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public String getThumbnail() { return thumbnail; }
    public List<ColourModel> getColours() { return colours; }
    public boolean isActive() { return isActive; }
}
