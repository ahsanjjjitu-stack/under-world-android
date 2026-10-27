package com.example.underworld.checkout_pack;

public class OrderRequest {
    private ShippingAddress shippingAddress;
    private PaymentInfo payment;

    public OrderRequest(ShippingAddress shippingAddress, PaymentInfo payment) {
        this.shippingAddress = shippingAddress;
        this.payment = payment;
    }

    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public PaymentInfo getPayment() { return payment; }
}
