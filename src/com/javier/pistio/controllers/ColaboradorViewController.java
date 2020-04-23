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
import static com.javier.pistio.utils.Util.addZeros;

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

    }

    @FXML
    void finalizar(MouseEvent event) {

    }

    @FXML
    void next(MouseEvent event) { // {_id, type: "C", correl: 1, pref: true}
        String req = "{" +
                "\"_id\": \"" + turno.get_id() + "\", " +
                "\"type\": \"" + turno.getType() + "\", " +
                "\"correl\": " + turno.getCorrel() + ", " +
                "\"pref\": " + turno.isPref() + ", " +
                "\"state\": " + "\"A\"" +
                "}";
        SOCKET.emit("atender", req);
    }

    @FXML
    void llamar(MouseEvent event) {
        SOCKET.emit("llamar", turno.getType(), ProjectVariable.username, turno.getType()+addZeros(turno.getCorrel()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(ProjectVariable.SERVICE);
        if (ProjectVariable.SERVICE.equals("Preferencias")) {
            rootPane.getChildren().remove(prefList);
            AnchorPane.setTopAnchor(turnList, 70.0);
        } else {
            SOCKET.emit("isPreferencesConnected");
        }

        initTable();

        String t = ProjectVariable.SERVICE.equals("Caja") ? "C" : ProjectVariable.SERVICE.equals("Atención al Cliente") ? "S" : ProjectVariable.SERVICE.equals("Créditos") ? "R" : ProjectVariable.SERVICE.equals("Gestor") ? "G" : "P";
        SOCKET.emit("init", t, false);
        SOCKET.emit("init", t, true);

        SOCKET.on("newTurn" + t, args -> {
            Type type = new TypeToken<List<Turno>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Turno> a = gson.fromJson(args[0].toString(), type);
            turnos.clear();
            turnos.addAll(a);
            Platform.runLater(() -> {
                turns.clear();
                for (Turno turno : a) {
                    turns.add(new Label(turno.getType() + addZeros(turno.getCorrel())));
                }
            });

        });

        SOCKET.on("newTurnP", args -> {
            Type type = new TypeToken<List<Turno>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Turno> a = gson.fromJson(args[0].toString(), type);
            turnosP.clear();
            turnosP.addAll(a);
            Platform.runLater(() -> {
                turnsP.clear();
                for (Turno turno : a) {
                    turnsP.add(new Label(turno.getType() + addZeros(turno.getCorrel())));
                }
            });

        });

        SOCKET.on("preferencesConnected", args -> {
            Platform.runLater(() -> {
                boolean resp = ((boolean) args[0]);
                if (resp) {
                    // Quitar Lista de Preferencias
                    rootPane.getChildren().remove(prefList);
                    AnchorPane.setTopAnchor(turnList, 70.0);
                } else {
                    // Poner lista de Preferencias
                    rootPane.getChildren().add(prefList);
                    AnchorPane.setTopAnchor(turnList, 220.0);
                }
            });
        });
    }

    private void initTable() {
        turnList.setItems(turns);
        prefList.setItems(turnsP);
    }
}
