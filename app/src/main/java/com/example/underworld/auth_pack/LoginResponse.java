package com.example.underworld.auth_pack;

public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private User user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }

    public static class User {
        private String id;
        private String name;
        private String email;
        private String fcmToken;
        private String avatar;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getFcmToken() { return fcmToken; }
        public String getAvatar() { return avatar; }
    }

}
