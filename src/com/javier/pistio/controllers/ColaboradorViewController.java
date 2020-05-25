package com.javier.pistio.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javier.pistio.modelos.Turno;
import com.javier.pistio.utils.ProjectVariable;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.ProjectVariable.type;
import static com.javier.pistio.utils.Util.addZeros;
import static com.javier.pistio.utils.Util.alert;

public class ColaboradorViewController implements Initializable {

    private final ObservableList<Label> turns = FXCollections.observableArrayList();
    private final ObservableList<Label> turnsP = FXCollections.observableArrayList();
    private final ArrayList<Turno> turnos = new ArrayList<>();
    private final ArrayList<Turno> turnosP = new ArrayList<>();
    private Turno turno = new Turno();

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label title;

    @FXML
    private JFXListView<Label> turnList, prefList;

    @FXML
    void noPrefClicked(MouseEvent event) {
        turno = turnos.get(turnList.getSelectionModel().getSelectedIndex());
        prefList.getSelectionModel().clearSelection();
    }

    @FXML
    void prefClicked(MouseEvent event) {
        turno = turnosP.get(prefList.getSelectionModel().getSelectedIndex());
        turnList.getSelectionModel().clearSelection();
    }

    @FXML
    void cancelar(MouseEvent event) {
        String req = "{" +
                "\"_id\": \"" + turno.get_id() + "\", " +
                "\"type\": \"" + turno.getType() + "\", " +
                "\"correl\": " + turno.getCorrel() + ", " +
                "\"pref\": " + turno.isPref() + ", " +
                "\"state\": " + "\"X\"" +
                "}";
        SOCKET.emit("atender", req);
        updateList();
    }

    @FXML
    void finalizar(MouseEvent event) {
        String req = "{" +
                "\"_id\": \"" + turno.get_id() + "\", " +
                "\"type\": \"" + turno.getType() + "\", " +
                "\"correl\": " + turno.getCorrel() + ", " +
                "\"pref\": " + turno.isPref() + ", " +
                "\"state\": " + "\"A\"" +
                "}";
        SOCKET.emit("atender", req);
        updateList();
    }

    @FXML
    void atender(MouseEvent event) { // {_id, type: "C", correl: 1, pref: true}
        String req = "{" +
                "\"_id\": \"" + turno.get_id() + "\", " +
                "\"type\": \"" + turno.getType() + "\", " +
                "\"correl\": " + turno.getCorrel() + ", " +
                "\"pref\": " + turno.isPref() + ", " +
                "\"state\": " + "\"G\"" +
                "}";
        SOCKET.emit("atender", req);
        SOCKET.emit("newClient");
        updateList();
    }

    @FXML
    void llamar(MouseEvent event) {
        SOCKET.emit("llamar", turno.getType(), ProjectVariable.username, turno.getType() + addZeros(turno.getCorrel()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(ProjectVariable.SERVICE);
        if (ProjectVariable.pref) {
            rootPane.getChildren().remove(turnList);
            AnchorPane.setBottomAnchor(prefList, 0.0);
        } else {
            SOCKET.emit("isPreferencesConnected");
        }

        initTable();

        updateList();

        SOCKET.on("newTurn" + type, args -> {
            Type type = new TypeToken<List<Turno>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Turno> list = gson.fromJson(args[0].toString(), type);
            updateTurns((boolean) args[1], list);
        });

        SOCKET.on("preferencesConnected", args -> {
            Platform.runLater(() -> {
                System.out.println(args[0]);
                if (args[1].toString().equals(type))
                    if (((boolean) args[0])) {
                        // Quitar Lista de Preferencias
                        rootPane.getChildren().remove(prefList);
                        AnchorPane.setTopAnchor(turnList, 70.0);
                        System.out.println("Ocultar");
                    } else {
                        // Poner lista de Preferencias
                        rootPane.getChildren().add(prefList);
                        AnchorPane.setTopAnchor(turnList, 220.0);
                        System.out.println("Mostrar");
                    }

            });
        });

        SOCKET.on("alert", args -> {
            Platform.runLater(() -> {
                alert(root, rootPane, "Alert de Tiempo", args[0].toString());
            });
        });
    }

    private void updateTurns(boolean isPref, ArrayList<Turno> list) {
        System.out.println(isPref);
        if (isPref) {
            turnosP.clear();
            turnosP.addAll(list);
            Platform.runLater(() -> {
                turnsP.clear();
                for (Turno turno : list) {
                    turnsP.add(new Label(turno.getType() + addZeros(turno.getCorrel())));
                }
            });
        } else {
            turnos.clear();
            turnos.addAll(list);
            Platform.runLater(() -> {
                turns.clear();
                for (Turno turno : list) {
                    turns.add(new Label(turno.getType() + addZeros(turno.getCorrel())));
                }
            });
        }
    }

    private void updateList() {
        SOCKET.emit("init", type, ProjectVariable.pref);
        if (!ProjectVariable.pref) SOCKET.emit("init", type, true);
    }

    private void initTable() {
        turnList.setItems(turns);
        prefList.setItems(turnsP);
    }
}
