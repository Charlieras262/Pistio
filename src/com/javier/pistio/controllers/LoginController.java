package com.javier.pistio.controllers;


import com.javier.pistio.utils.ProjectTypes;
import com.javier.pistio.utils.ProjectVariable;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
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
import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.ProjectVariable.initSocket;
import static com.javier.pistio.utils.Util.alert;
import static com.javier.pistio.utils.Util.changeView;

public class LoginController implements Initializable {

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
            SOCKET.emit("login", user.getText(), pass.getText(), ProjectVariable.SERVICE == ProjectTypes.ADMIN ? "A" : "S");
        }else{
            alert(root, rootPane,"Error", "Debe llenar los campos de las credenciales");
        }
    }

    @FXML
    void back(MouseEvent event) {
        changeView(root, rootPane, "../ui/main.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(SOCKET == null){
            initSocket(args -> {
                Platform.runLater(() -> {
                    if(!(boolean)args[0]){
                        dialog.close();
                        alert(root, rootPane,"Error", "Credenciales Invalidas");
                    }else{
                        dialog.close();
                        user.clear();
                        pass.clear();
                        try {
                            StackPane anchorPane = FXMLLoader.load(getClass().getResource((ProjectVariable.SERVICE == ProjectTypes.ADMIN ? "../ui/admin_menu.fxml" : "../ui/admin_menu.fxml")));
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
            if(ProjectVariable.SERVICE == ProjectTypes.ADMIN){
                title.setText("Login Admin");
            } else {
                title.setText("Login Soporte");
            }
        }
    }
}
