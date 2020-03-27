package com.javier.pistio.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.Util.changeView;

public class SoporteMenuController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    void back(MouseEvent event) {
        changeView(root, rootPane, "../ui/login.fxml");
    }

    @FXML
    void generarReporte(MouseEvent event) {

    }

    @FXML
    void ticketView(MouseEvent event) {
        changeView(root, rootPane, "../ui/ticket_view.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
