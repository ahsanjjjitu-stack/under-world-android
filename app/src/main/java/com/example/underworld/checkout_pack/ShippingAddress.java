package com.example.underworld.checkout_pack;

public class ShippingAddress {
    private String fullName;
    private String phone;
    private String district;
    private String area;
    private String addressLine;

    public ShippingAddress(String fullName, String phone, String district, String area, String addressLine) {
        this.fullName = fullName;
        this.phone = phone;
        this.district = district;
        this.area = area;
        this.addressLine = addressLine;
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getDistrict() { return district; }
    public String getArea() { return area; }
    public String getAddressLine() { return addressLine; }
}
