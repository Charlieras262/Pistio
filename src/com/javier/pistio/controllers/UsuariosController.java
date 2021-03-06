package com.javier.pistio.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javier.pistio.modelos.Soporte;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

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
    private TableColumn<Soporte, String> cId, cNombre, cPass, cApellido, cUsuario, cType, cPrefs;

    @FXML private JFXButton add, nuevo, del, mod;

    @FXML
    private JFXComboBox<Label> type;

    @FXML
    private JFXToggleButton prefs;

    private JFXDialog dialog;

    private boolean preferencias = false;

    @FXML
    void back(MouseEvent event) {
        if (!add.isDisable() || !mod.isDisable()){
            limpiar();
            add.setDisable(true);
            nuevo.setDisable(false);
            toggleDisableFields(false);
        } else {
            changeView(root, rootPane, "../ui/admin_menu.fxml", true);
        }
    }

    @FXML
    void crear(ActionEvent event) {
        dialog = alert(root, rootPane,"Creando Usuario", null);
        Soporte soporte = newSoporte();
        soporte.setType(type.getValue().getText().equals("Caja") ? "C" : type.getValue().getText().equals("Atención al Cliente") ? "S" : type.getValue().getText().equals("Créditos") ? "R" : type.getValue().getText().equals("Gestor") ? "G" : "P");
        limpiar();
        add.setDisable(true);
        nuevo.setDisable(false);
        toggleDisableFields(false);
        SOCKET.emit("createUser", soporte);
    }

    @FXML
    void eliminar(ActionEvent event) {
        dialog = alert(root, rootPane,"Eliminando Usuario", null);
        SOCKET.emit("eliminarUsuario", getUsuarioSeleccionado().getId());
        limpiar();
        add.setDisable(true);
        nuevo.setDisable(false);
        toggleDisableFields(false);
    }

    @FXML
    void modificar(ActionEvent event) {
        dialog = alert(root, rootPane,"Modificando Usuario", null);
        Soporte soporte = newSoporte();
        soporte.setId(getUsuarioSeleccionado().getId());
        soporte.setType(type.getValue().getText().equals("Caja") ? "C" : type.getValue().getText().equals("Atención al Cliente") ? "S" : type.getValue().getText().equals("Créditos") ? "R" : type.getValue().getText().equals("Gestor") ? "G" : "T");

        SOCKET.emit("modificarUsuario", soporte.toJSON());
        limpiar();
        add.setDisable(true);
        nuevo.setDisable(false);
        toggleDisableFields(false);
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
        toggleDisableFields(true);
        prefs.setDisable(false);
        type.getSelectionModel().clearSelection();
        dataTable.getSelectionModel().clearSelection();
    }

    private Soporte newSoporte(){
        return new Soporte(nombre.getText(), apellido.getText(), usuario.getText(), pass.getText(), prefs.isSelected());
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
        cType.setCellValueFactory(param -> param.getValue().typeProperty());
        cPrefs.setCellValueFactory(param -> param.getValue().prefsProperty());

        dataTable.setItems(usuarios);

        final ObservableList<Soporte> tablaPersonaSel = dataTable.getSelectionModel().getSelectedItems();
        tablaPersonaSel.addListener((ListChangeListener<Soporte>) c -> setSelectedUser());
    }

    private void setSelectedUser() {
        final Soporte soporte = getUsuarioSeleccionado();

        if (soporte != null) {

            toggleDisableFields(true);

            // Pongo los textFields con los datos correspondientes
            nombre.setText(soporte.getNombre());
            apellido.setText(soporte.getApellido());
            usuario.setText(soporte.getUsuario());
            pass.setText(soporte.getPass());
            type.getSelectionModel().select(soporte.getType().equals("C") ? 0 : soporte.getType().equals("S") ? 1 : soporte.getType().equals("R") ? 2 : soporte.getType().equals("G") ? 3 : 4);
            prefs.setSelected(soporte.isPref());

            // Pongo los botones en su estado correspondiente
            mod.setDisable(false);
            del.setDisable(false);
            add.setDisable(true);
            prefs.setDisable(false);
        }
    }

    private Label createLabel(String text){
        return new Label(text);
    }

    private void toggleDisableFields(boolean status){
        nombre.setDisable(!status);
        apellido.setDisable(!status);
        usuario.setDisable(!status);
        pass.setDisable(!status);
        type.setDisable(!status);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SOCKET.emit("init");
        SOCKET.on("users", args -> {
            Type type = new TypeToken<List<Soporte>>() {}.getType();
            Gson gson = new Gson();
            ArrayList<Soporte> a = gson.fromJson(args[0].toString(), type);
            usuarios.setAll(a);
            Platform.runLater(() -> {
                if(dialog != null) dialog.close();
            });
        });
        ObservableList<Label> tipos = FXCollections.observableArrayList(createLabel("Caja"), createLabel("Atención al Cliente"), createLabel("Créditos"), createLabel("Gestor"), createLabel("Turno"));
        type.setItems(tipos);
        inicializarTabla();
    }
}
