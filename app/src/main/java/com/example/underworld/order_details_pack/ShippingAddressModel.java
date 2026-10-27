package com.example.underworld.order_details_pack;

import com.google.gson.annotations.SerializedName;

public class ShippingAddressModel {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("district")
    private String district;

    @SerializedName("area")
    private String area;

    @SerializedName("addressLine")
    private String addressLine;

    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getDistrict() { return district; }
    public String getArea() { return area; }
    public String getAddressLine() { return addressLine; }
}
