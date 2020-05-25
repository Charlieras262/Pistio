package com.javier.pistio.utils;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class ProjectVariable {

    public static String type, username;
    public static boolean pref = false;
    public static String SERVICE = "View";
    public static Socket SOCKET;

    public static void initSocket(){
        try {
            SOCKET = IO.socket("http://localhost:8080");
            SOCKET.on(Socket.EVENT_CONNECT, args -> {
                SOCKET.emit("connected", ProjectVariable.class.hashCode());
            });
            SOCKET.connect();
        } catch (URISyntaxException e) {
            System.out.println("Error: " + e);
        }
    }
}
