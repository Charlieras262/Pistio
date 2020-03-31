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

import static com.javier.pistio.utils.Util.changeView;

public class AdminMenuController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    void back(MouseEvent event) {
        try {
            StackPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/login.fxml"));
            rootPane.getChildren().setAll(anchorPane);
        } catch (IOException e) {
            System.err.println("Error: No se econtro el archivo.");
        }
    }

    @FXML
    void go2Users(MouseEvent event) {
        changeView(root, rootPane, "../ui/usuarios.fxml", true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
