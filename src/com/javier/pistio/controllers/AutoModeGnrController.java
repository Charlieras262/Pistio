package com.javier.pistio.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.*;
import static com.javier.pistio.utils.Util.*;

public class AutoModeGnrController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                playAudio("../media/audios/gnr.wav");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        SOCKET.emit("createTransaction", type, pref);

        SOCKET.on("newTransaction", args -> {
            new Thread(() -> {
                try {
                    Thread.sleep(3500);
                    playAudio("../media/audios/esp.wav");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            generatePDF(args[0].toString());
            Platform.runLater(() -> {
                changeView(root, null, "../ui/auto_mode_start.fxml", false);
            });
        });
    }
}
