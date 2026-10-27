package com.example.underworld.cart_pack;

import java.io.Serializable;
import java.util.List;

public class CartColourModel implements Serializable {
    private String name;
    private String hex;
    private String image;

    public CartColourModel(String name, String hex, String image) {
        this.name = name;
        this.hex = hex;
        this.image = image;
    }

    public String getName() { return name; }
    public String getHex() { return hex; }
    public String getImage() { return image; }
}
