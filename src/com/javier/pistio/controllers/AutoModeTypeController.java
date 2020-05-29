package com.javier.pistio.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.type;
import static com.javier.pistio.utils.Util.*;

public class AutoModeTypeController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXComboBox<Label> tipo;

    @FXML
    void next(MouseEvent event) {
        type = tipo.getSelectionModel().getSelectedItem().getText();
        switch (type){
            case "Caja":
                type = "C";
                playAudio("../media/audios/caja.wav");
                break;
            case "Créditos":
                type = "R";
                playAudio("../media/audios/creditos.wav");
                break;
            case "Atención al Cliente":
                type = "S";
                playAudio("../media/audios/ac.wav");
                break;
        }
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    changeView(root, null, "../ui/auto_mode_pref.fxml", false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                playAudio("../media/audios/transaction.wav");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        ObservableList<Label> tipos = FXCollections.observableArrayList(createLabel("Caja"), createLabel("Atención al Cliente"), createLabel("Créditos"));
        tipo.setItems(tipos);
    }
}
