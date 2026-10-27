package com.example.underworld.order_details_pack;

import com.google.gson.annotations.SerializedName;

public class ColourModelOrder {
    @SerializedName("name")
    private String name;

    @SerializedName("hex")
    private String hex;

    @SerializedName("image")
    private String image;

    public String getName() { return name; }
    public String getHex() { return hex; }
    public String getImage() { return image; }
}
