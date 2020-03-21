package com.javier.pistio.controllers;

import com.javier.pistio.utils.ProjectTypes;
import com.javier.pistio.utils.ProjectVariable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.Util.changeView;

public class MainController implements Initializable {

    @FXML
    private StackPane root;


    @FXML
    public void admin(ActionEvent event){
        ProjectVariable.SERVICE = ProjectTypes.ADMIN;
        changeView(root, null, "../ui/login.fxml");
    }

    @FXML
    public void support(ActionEvent event){
        ProjectVariable.SERVICE = ProjectTypes.SUPPORT;
        changeView(root, null, "../ui/login.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
