package com.javier.pistio.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javier.pistio.modelos.Soporte;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.ProjectVariable.SOCKET;
import static com.javier.pistio.utils.Util.alert;
import static com.javier.pistio.utils.Util.changeView;

public class UsuariosController implements Initializable {

    private ObservableList<Soporte> usuarios = FXCollections.observableArrayList();

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField nombre, apellido, usuario;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private TableView<Soporte> dataTable;

    @FXML
    private TableColumn<Soporte, String> cId, cNombre, cPass, cApellido, cUsuario;

    @FXML private JFXButton add, nuevo, del, mod;

    private JFXDialog dialog;

    @FXML
    void back(MouseEvent event) {
        if (!add.isDisable() || !mod.isDisable()){
            limpiar();
            add.setDisable(true);
            nuevo.setDisable(false);
        } else {
            changeView(root, rootPane, "../ui/admin_menu.fxml");
        }
    }

    @FXML
    void crear(ActionEvent event) {
        dialog = alert(root, rootPane,"Creando Usuario", null);
        Soporte soporte = new Soporte(nombre.getText(), apellido.getText(), usuario.getText(), pass.getText());
        limpiar();
        SOCKET.emit("createUser", soporte);
        add.setDisable(true);
        nuevo.setDisable(false);
    }

    @FXML
    void eliminar(ActionEvent event) {
        dialog = alert(root, rootPane,"Eliminando Usuario", null);
        SOCKET.emit("eliminarUsuario", getUsuarioSeleccionado().getId());
        limpiar();
        add.setDisable(true);
        nuevo.setDisable(false);
    }

    @FXML
    void modificar(ActionEvent event) {
        dialog = alert(root, rootPane,"Modificando Usuario", null);
        Soporte soporte = new Soporte(nombre.getText(), apellido.getText(), usuario.getText(), pass.getText());
        soporte.setId(getUsuarioSeleccionado().getId());
        SOCKET.emit("modificarUsuario", soporte.toJSON());
        System.out.println(soporte.toJSON());
        limpiar();
        add.setDisable(true);
        nuevo.setDisable(false);
    }

    @FXML
    void nuevo(ActionEvent event) {
        limpiar();
    }

    private void limpiar(){
        nombre.clear();
        apellido.clear();
        usuario.clear();
        pass.clear();
        nuevo.setDisable(true);
        add.setDisable(false);
        mod.setDisable(true);
        del.setDisable(true);
        dataTable.getSelectionModel().clearSelection();
    }

    public Soporte getUsuarioSeleccionado() {
        if (dataTable != null) {
            List<Soporte> tabla = dataTable.getSelectionModel().getSelectedItems();
            if (tabla.size() == 1) {
                return tabla.get(0);
            }
        }
        return null;
    }

    private void inicializarTabla(){
        cId.setCellValueFactory(param -> param.getValue().idProperty());
        cNombre.setCellValueFactory(param -> param.getValue().nombreProperty());
        cApellido.setCellValueFactory(param -> param.getValue().apellidoProperty());
        cUsuario.setCellValueFactory(param -> param.getValue().usuarioProperty());
        cPass.setCellValueFactory(param -> param.getValue().passProperty());

        dataTable.setItems(usuarios);

        final ObservableList<Soporte> tablaPersonaSel = dataTable.getSelectionModel().getSelectedItems();
        tablaPersonaSel.addListener((ListChangeListener<Soporte>) c -> ponerPersonaSeleccionada());
    }

    private void ponerPersonaSeleccionada() {
        final Soporte soporte = getUsuarioSeleccionado();

        if (soporte != null) {

            // Pongo los textFields con los datos correspondientes
            nombre.setText(soporte.getNombre());
            apellido.setText(soporte.getApellido());
            usuario.setText(soporte.getUsuario());
            pass.setText(soporte.getPass());

            // Pongo los botones en su estado correspondiente
            mod.setDisable(false);
            del.setDisable(false);
            add.setDisable(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SOCKET.emit("users", "");
        SOCKET.on("users", args -> {
            Type type = new TypeToken<List<Soporte>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<Soporte> a = gson.fromJson(args[0].toString(), type);
            usuarios.setAll(a);
            Platform.runLater(() -> {
                if(dialog != null) dialog.close();
            });
        });
        inicializarTabla();
    }
}
