package com.javier.pistio.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    void back(MouseEvent event) {
        try {
            StackPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/login.fxml"));
            root.getChildren().setAll(anchorPane);
        } catch (IOException e) {
            System.err.println("Error: No se econtro el archivo.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
