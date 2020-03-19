package com.javier.pistio.controllers;


import com.javier.pistio.utils.ProjectTypes;
import com.javier.pistio.utils.ProjectVariable;
import com.jfoenix.controls.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private Text title;

    private JFXDialog dialog;

    @FXML
    void login(ActionEvent event) {
        if(!user.getText().equals("") && !pass.getText().equals("")){
            dialog = alert(root, "Cargando", null);
            socket.emit(ProjectVariable.SERVICE == ProjectTypes.ADMIN ? "loginAdmin" : "loginSupport", user.getText(), pass.getText());
        }else{
            alert(root, "Error", "Debe llenar los campos de las credenciales");
        }
    }

    @FXML
    void back(MouseEvent event) {
        try {
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/main.fxml"));
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
                       alert(root, "Error", "Credenciales Invalidas");
                   }else{
                       dialog.close();
                       user.clear();
                       pass.clear();
                       alert(root, "Correcto", "Session Iniciada como " + (ProjectVariable.SERVICE == ProjectTypes.ADMIN ? "Administrador" : "Soporte"));
                       try {
                           AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("../ui/admin_menu.fxml"));
                           rootPane.getChildren().setAll(anchorPane);
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

    public JFXDialog alert(StackPane context, String textHeader, String textBody) {
        BoxBlur blur = new BoxBlur(3,3,3);

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(context, content, JFXDialog.DialogTransition.CENTER);
        content.setHeading(new Text(textHeader));
        JFXSpinner spinner = new JFXSpinner();
        spinner.setPrefSize(30, 30);
        if(textBody != null){
            content.setBody(new Text(textBody));
            JFXButton actBut = new JFXButton("OK");
            actBut.setOnAction(event -> {
                dialog.close();
            });
            repeatFocus(actBut);
            content.setActions(actBut);
        }else{
            repeatFocus(spinner);
            content.setBody(spinner);
            dialog.setOverlayClose(false);
        }

        dialog.setOnDialogClosed(event -> {
            rootPane.setEffect(null);
        });


        dialog.show();

        rootPane.setEffect(blur);
        return dialog;
    }

    private void repeatFocus(Node node) {
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                repeatFocus(node);
            }
        });
    }
}
