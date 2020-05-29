package com.javier.pistio.controllers;

import com.javier.pistio.utils.ProjectVariable;
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

public class AutoModePrefController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXComboBox<Label> pref;

    @FXML
    void next(MouseEvent event) {
        ProjectVariable.pref = !pref.getSelectionModel().getSelectedItem().getText().equals("Sin Preferencias");
        String type = pref.getSelectionModel().getSelectedItem().getText();
        switch (type){
            case "Sin Preferencias":
                playAudio("../media/audios/np.wav");
                break;
            case "Adulto Mayor":
                playAudio("../media/audios/am.wav");
                break;
            case "Discapacidad":
                playAudio("../media/audios/dc.wav");
                break;
            default:
                playAudio("../media/audios/em.wav");
                break;
        }
        new Thread(() -> {
            try {
                Thread.sleep(2500);
                Platform.runLater(() -> {
                    changeView(root, null, "../ui/auto_mode_gnr.fxml", false);
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
                playAudio("../media/audios/preferences.wav");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        ObservableList<Label> prefs = FXCollections.observableArrayList(createLabel("Sin Preferencias"), createLabel("Adulto Mayor"), createLabel("Discapacidad"), createLabel("Embarazo"));

        pref.setItems(prefs);
    }
}
