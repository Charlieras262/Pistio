package com.javier.pistio.controllers;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.ProjectVariable.SERVICE;
import static com.javier.pistio.utils.ProjectVariable.type;
import static com.javier.pistio.utils.ProjectVariable.username;
import static com.javier.pistio.utils.ProjectVariable.pref;
import static com.javier.pistio.utils.ProjectVariable.initSocket;
import static com.javier.pistio.utils.Util.alert;

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
        if(!user.getText().isEmpty() && !pass.getText().isEmpty()){
            dialog = alert(root, rootPane,"Cargando", null);
            SOCKET.emit("login", user.getText(), pass.getText());
        }else{
            alert(root, rootPane,"Error", "Debe llenar los campos");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(SOCKET == null){
            initSocket();
            SOCKET.on("logged", args -> {
                Platform.runLater(() -> {
                    if(!(boolean)args[0]){
                        alert(root, rootPane,"Error", args[2].toString());
                        closeDialog();
                    }else{
                        closeDialog();
                        user.clear();
                        pass.clear();
                        try {
                            String url;
                            char type = args[1].toString().charAt(0);
                            switch (type){
                                case 'A': url = "../ui/admin_menu.fxml"; break;
                                case 'G': url = "../ui/soporte_menu.fxml"; break;
                                case 'C': setCurrentUser("Caja", args[3].toString(), String.valueOf(type), (boolean) args[4]); url = "../ui/colaborador_view.fxml"; break;
                                case 'S': setCurrentUser("Atención al Cliente", args[3].toString(), String.valueOf(type), (boolean) args[4]); url = "../ui/colaborador_view.fxml"; break;
                                case 'R': setCurrentUser("Créditos", args[3].toString(), String.valueOf(type), (boolean) args[4]); url = "../ui/colaborador_view.fxml"; break;
                                case 'T': url = "../ui/turno_view.fxml"; break;
                                default: url = "../ui/login.fxml";
                            }
                            StackPane anchorPane = FXMLLoader.load(getClass().getResource(url));
                            if(rootPane != null)
                                rootPane.getChildren().setAll(anchorPane);
                            else
                                root.getChildren().setAll(anchorPane);
                        } catch (IOException e) {
                            System.err.println("Error: No se econtro el archivo." + e.getMessage());
                        }
                    }
                });
            });
        }
    }

    private void closeDialog(){
        if(dialog == null){
            new Thread(() -> {
                while(true){
                    if(dialog != null){
                        dialog.close();
                        break;
                    }
                }
            }).start();
        }else{
            dialog.close();
        }
    }

    private void setCurrentUser(String service, String name, String t, boolean p){
        String prefStr = p ? " de Preferencias" : "";
        SERVICE = service + prefStr;
        username = name;
        type = t;
        pref = p;
    }
}
