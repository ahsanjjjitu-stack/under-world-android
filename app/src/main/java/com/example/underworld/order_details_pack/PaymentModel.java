package com.example.underworld.order_details_pack;

import com.google.gson.annotations.SerializedName;

public class PaymentModel {
    @SerializedName("method")
    private String method;

    @SerializedName("senderNumber")
    private String senderNumber;

    @SerializedName("transactionId")
    private String transactionId;

    public String getMethod() { return method; }
    public String getSenderNumber() { return senderNumber; }
    public String getTransactionId() { return transactionId; }
}
