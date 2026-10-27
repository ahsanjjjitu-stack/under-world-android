package com.example.underworld.checkout_pack;

public class PaymentInfo {
    private String method; // "bkash", "nagad", "cod"
    private String senderNumber;
    private String transactionId;

    public PaymentInfo(String method, String senderNumber, String transactionId) {
        this.method = method;
        this.senderNumber = senderNumber;
        this.transactionId = transactionId;
    }

    // Getters and Setters
    public String getMethod() { return method; }
    public String getSenderNumber() { return senderNumber; }
    public String getTransactionId() { return transactionId; }
}
