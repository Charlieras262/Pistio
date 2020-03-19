package com.javier.pistio.controllers;

import com.javier.pistio.utils.ProjectTypes;
import com.javier.pistio.utils.ProjectVariable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    public void admin(ActionEvent event){
        ProjectVariable.SERVICE = ProjectTypes.ADMIN;
        try {
            StackPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/login.fxml"));
            root.getChildren().setAll(anchorPane);
        } catch (IOException e) {
            System.err.println("Error: No se econtro el archivo.");
        }
    }

    @FXML
    public void support(ActionEvent event){
        ProjectVariable.SERVICE = ProjectTypes.SUPPORT;
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
