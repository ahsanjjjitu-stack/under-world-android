package com.example.underworld;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class SocketManager {
    private static Socket socket;
    private static final String SERVER_URL = "https://under-world-backend.onrender.com/";
    private static String currentUserId;

    public static void connectSocket(String userId) {
        currentUserId = userId;

        try {
            if (socket == null) {
                IO.Options options = new IO.Options();
                options.reconnection = true; // Auto reconnect enable kora bhalo
                options.reconnectionAttempts = Integer.MAX_VALUE;
                options.reconnectionDelay = 1000;

                socket = IO.socket(SERVER_URL, options);

                // Connect listener
                socket.on(Socket.EVENT_CONNECT, args -> {
                    System.out.println("✅ Socket Connected! ID: " + socket.id());
                    registerUser();
                });

                // Disconnect listener
                socket.on(Socket.EVENT_DISCONNECT, args -> {
                    System.out.println("❌ Socket Disconnected!");
                });

                // Connection Error listener (Render server sleep thakle ekhane error asbe)
                socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                    if (args.length > 0) {
                        System.out.println("⚠️ Connection Error: " + args[0]);
                    }
                });
            }

            if (!socket.connected()) {
                socket.connect();
            } else {
                // Jodi already connected thake
                registerUser();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser() {
        if (currentUserId != null && !currentUserId.isEmpty() && socket != null && socket.connected()) {
            socket.emit("register_user", currentUserId);
            System.out.println("🚀 Emitted 'register_user' for userId: " + currentUserId);
        } else {
            System.out.println("⚠️ Could not register user. UserId or Socket is not ready.");
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void disconnectSocket() {
        if (socket != null) {
            socket.disconnect();
            socket.off(); // Sob listener remove kore dewa bhalo
            socket = null;
            currentUserId = null;
            System.out.println("🔌 Socket disconnected completely.");
        }
    }
}