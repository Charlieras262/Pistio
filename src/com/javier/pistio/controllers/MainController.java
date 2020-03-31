package com.javier.pistio.controllers;

import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.Util.changeView;

public class MainController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private JFXToggleButton pref;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() ->{
            try {
                Thread.sleep(500);
                Platform.runLater(() -> changeView(root, null, "../ui/login.fxml",false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
