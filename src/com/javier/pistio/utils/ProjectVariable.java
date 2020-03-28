package com.javier.pistio.utils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class ProjectVariable {
    public static ProjectTypes SERVICE = ProjectTypes.ADMIN;
    public static final String WM = "WATERMARK";
    public static final String HEADER = "header||HEADER";
    public static Socket SOCKET;

    public static void initSocket(Emitter.Listener listener){
        try {
            SOCKET = IO.socket("http://localhost:8080");
            SOCKET.on(Socket.EVENT_CONNECT, args -> {
                SOCKET.emit("connected", ProjectVariable.class.hashCode());
            }).on("logged", listener);
            SOCKET.connect();
        } catch (URISyntaxException e) {
            System.out.println("Error: " + e);
        }
    }
}
