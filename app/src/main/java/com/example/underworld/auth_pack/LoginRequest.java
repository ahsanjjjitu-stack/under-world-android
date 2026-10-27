package com.example.underworld.auth_pack;

public class LoginRequest {

    private String idToken;
    private String fcmToken;

    public LoginRequest(String idToken, String fcmToken) {
        this.idToken = idToken;
        this.fcmToken = fcmToken;
    }

    public String getIdToken() { return idToken; }
    public String getFcmToken() { return fcmToken; }

}
