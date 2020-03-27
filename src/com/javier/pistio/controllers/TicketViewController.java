package com.javier.pistio.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

import static com.javier.pistio.utils.Util.changeView;

public class TicketViewController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXComboBox<Label> tipo, pref;

    @FXML
    void back(MouseEvent event) {
        changeView(root, rootPane, "../ui/soporte_menu.fxml");
    }

    @FXML
    void generarTicket(MouseEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Label> tipos = FXCollections.observableArrayList(createLable("Caja"), createLable("Servicio al Cliente"), createLable("Creditos"));
        ObservableList<Label> prefs = FXCollections.observableArrayList(createLable("Sin Preferencias"), createLable("Adulto Mayor"), createLable("Discapacidad"), createLable("Embarazo"));

        tipo.setItems(tipos);
        pref.setItems(prefs);

    }

    private void setConverter(JFXComboBox<Label> comboBox) {
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Label object) {
                System.out.println(object == null ? "" : object.getText());
                return object == null ? "" : object.getText();
            }

            @Override
            public Label fromString(String string) {
                return new Label(string);
            }
        });
    }

    private Label createLable(String text){
        return new Label(text);
    }
}
