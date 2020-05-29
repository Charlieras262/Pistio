package com.javier.pistio.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javier.pistio.modelos.Turno;
import com.javier.pistio.utils.ProjectVariable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.ProjectVariable.type;
import static com.javier.pistio.utils.Util.addZeros;

public class TurnoViewController implements Initializable {

    private final ObservableList<Label> turns = FXCollections.observableArrayList();

    @FXML
    private ListView<Label> turnos;

    @FXML
    private Label turno;

    @FXML
    private Label lugar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SOCKET.emit("init", "T", false);

        turnos.setItems(turns);

        SOCKET.on("newTurn" + type, args -> {
            Type type = new TypeToken<List<Turno>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Turno> list = gson.fromJson(args[0].toString(), type);
            Platform.runLater(() -> {
                turns.clear();
                for (Turno turno : list) {
                    turns.add(new Label(turno.getType() + addZeros(turno.getCorrel())));
                }
            });
        });

        SOCKET.on("llamarTicket", args -> {
            Platform.runLater(() -> {
                turno.setText(args[0].toString());
                lugar.setText(args[1].toString());
            });
        });
    }
}
