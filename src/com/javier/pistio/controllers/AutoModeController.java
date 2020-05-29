package com.javier.pistio.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.Util.changeView;
import static com.javier.pistio.utils.Util.playAudio;

public class AutoModeController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    void goToTransactionType(MouseEvent event) {
        playAudio("../media/audios/welcome.wav");
        new Thread(() -> {
            try {
                Thread.sleep(2500);
                Platform.runLater(() -> {
                    changeView(root, null, "../ui/auto_mode_type.fxml", false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
