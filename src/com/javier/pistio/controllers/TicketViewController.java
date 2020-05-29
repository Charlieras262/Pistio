package com.javier.pistio.controllers;

import com.jfoenix.controls.JFXComboBox;
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

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.Util.*;

public class TicketViewController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXComboBox<Label> tipo, pref;

    @FXML
    void back(MouseEvent event) {
        changeView(root, rootPane, "../ui/soporte_menu.fxml", true);
    }

    @FXML
    void modeAuto(MouseEvent event) {
        changeView(root, rootPane, "../ui/auto_mode_start.fxml", true);
    }

    @FXML
    void generarTicket(MouseEvent event) {
        String type = (tipo.getValue().getText().equals("Caja") ? "C" : tipo.getValue().getText().equals("Atención al Cliente") ? "S" : "R");
        boolean isPref = !pref.getValue().getText().equals("Sin Preferencias");
        tipo.getSelectionModel().clearSelection();
        pref.getSelectionModel().clearSelection();
        SOCKET.emit("createTransaction", type, isPref);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Label> tipos = FXCollections.observableArrayList(createLabel("Caja"), createLabel("Atención al Cliente"), createLabel("Créditos"));
        ObservableList<Label> prefs = FXCollections.observableArrayList(createLabel("Sin Preferencias"), createLabel("Adulto Mayor"), createLabel("Discapacidad"), createLabel("Embarazo"));

        tipo.setItems(tipos);
        pref.setItems(prefs);

        SOCKET.on("newTransaction", args -> {
            generatePDF(args[0].toString());
        });
    }
}
