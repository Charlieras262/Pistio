package com.javier.pistio.controllers;


import com.javier.pistio.utils.ProjectTypes;
import com.javier.pistio.utils.ProjectVariable;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.Util.alert;

public class LoginController implements Initializable {

    private Socket socket;

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField user;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private Label title;

    private JFXDialog dialog;

    @FXML
    void login(ActionEvent event) {
        if(!user.getText().equals("") && !pass.getText().equals("")){
            dialog = alert(root, rootPane,"Cargando", null);
            socket.emit(ProjectVariable.SERVICE == ProjectTypes.ADMIN ? "loginAdmin" : "loginSupport", user.getText(), pass.getText());
        }else{
            alert(root, rootPane,"Error", "Debe llenar los campos de las credenciales");
        }
    }

    @FXML
    void back(MouseEvent event) {
        try {
            StackPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/main.fxml"));
            rootPane.getChildren().setAll(anchorPane);
        } catch (IOException e) {
            System.err.println("Error: No se econtro el archivo.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = IO.socket("http://localhost:8080");
            socket.on(Socket.EVENT_CONNECT, args -> {
                socket.emit("connected", hashCode());
            }).on("logged", args -> {
                Platform.runLater(() -> {
                   if(!(boolean)args[0]){
                       dialog.close();
                       alert(root, rootPane,"Error", "Credenciales Invalidas");
                   }else{
                       dialog.close();
                       user.clear();
                       pass.clear();
                       alert(root, rootPane,"Correcto", "Session Iniciada como " + (ProjectVariable.SERVICE == ProjectTypes.ADMIN ? "Administrador" : "Soporte"));
                       try {
                           StackPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/admin_menu.fxml"));
                           if(rootPane != null)
                               rootPane.getChildren().setAll(anchorPane);
                           else
                               root.getChildren().setAll(anchorPane);
                       } catch (IOException e) {
                           System.err.println("Error: No se econtro el archivo.");
                       }
                   }
                });
            });
            socket.connect();
        } catch (URISyntaxException e) {
            System.out.println("Error: " + e);
        }
        if(ProjectVariable.SERVICE == ProjectTypes.ADMIN){
            title.setText("Login Admin");
        } else {
            title.setText("Login Soporte");
        }
    }
}
