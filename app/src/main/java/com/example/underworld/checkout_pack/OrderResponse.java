package com.example.underworld.checkout_pack;

import java.io.Serializable;

public class OrderResponse {
    private boolean success;
    private String message;
    private Object data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}
