package com.javier.pistio.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javier.pistio.modelos.Soporte;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.Util.changeView;

public class UsuariosController implements Initializable {

    private ArrayList<Soporte> usuarios = new ArrayList<>();

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField nombre;

    @FXML
    private JFXTextField apellido;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXTextField usuario;

    @FXML
    void back(MouseEvent event) {
        changeView(root, rootPane, "../ui/admin_menu.fxml");
    }

    @FXML
    void cancelar(ActionEvent event) {

    }

    @FXML
    void crear(ActionEvent event) {
        Soporte soporte = new Soporte(nombre.getText(), apellido.getText(), usuario.getText(), pass.getText());
        limpiar();
        SOCKET.emit("createUser", soporte);
    }

    private void limpiar(){
        nombre.clear();
        apellido.clear();
        usuario.clear();
        pass.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SOCKET.on("users", args -> {
            Type type = new TypeToken<List<Soporte>>() {}.getType();
            Gson gson = new Gson();
            usuarios = gson.fromJson(args[0].toString(), type);
            System.out.println(usuarios);
        });
    }
}
