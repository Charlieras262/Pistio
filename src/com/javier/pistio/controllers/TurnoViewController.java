package com.javier.pistio.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;

public class TurnoViewController implements Initializable {

    @FXML
    private ListView<?> turnos;

    @FXML
    private Label turno;

    @FXML
    private Label lugar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SOCKET.on("llamarTicket", args -> {
            Platform.runLater(() -> {
                turno.setText(args[0].toString());
                lugar.setText(args[1].toString());
            });
        });
    }
}
