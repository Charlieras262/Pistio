package com.javier.pistio.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class Util {
    public static void changeView(StackPane root, AnchorPane rootPane, String url) {
        JFXDialog dialog = alert(root, rootPane, "Cargando", null);
        new Thread(() -> {
            try {
                Node anchorPane = FXMLLoader.load(Util.class.getResource(url));
                Platform.runLater(() -> {
                    if(rootPane != null)
                        rootPane.getChildren().setAll(anchorPane);
                    else
                        root.getChildren().clear();
                        root.getChildren().add(anchorPane);
                });
            } catch (IOException e) {
                Platform.runLater(() -> alert(root, rootPane, "Error", "Error al cargar el archivo especificado en la ruta \"" + url + "\""));
                System.err.println("Error: " + e);
            }
            Platform.runLater(dialog::close);
        }).start();
    }

    public static JFXDialog alert(StackPane context, AnchorPane fx, String textHeader, String textBody) {
        BoxBlur blur = new BoxBlur(3,3,3);

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(context, content, JFXDialog.DialogTransition.CENTER);
        content.setHeading(new Text(textHeader));
        JFXSpinner spinner = new JFXSpinner();
        spinner.setPrefSize(30, 30);
        if(textBody != null){
            Label l = new Label(textBody);
            l.getStyleClass().add("alert-txt");
            content.setBody(l);
            JFXButton actBut = new JFXButton("OK");
            actBut.setOnAction(event -> {
                dialog.close();
            });
            repeatFocus(actBut);
            actBut.getStyleClass().add("btn");
            content.setActions(actBut);
        }else{
            repeatFocus(spinner);
            content.setBody(spinner);
            dialog.setOverlayClose(false);
        }

        dialog.setOnDialogClosed(event -> {
            if(fx != null)
                fx.setEffect(null);
        });


        dialog.show();

        if(fx != null)
            fx.setEffect(blur);
        return dialog;
    }

    private static void repeatFocus(Node node) {
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                repeatFocus(node);
            }
        });
    }
}
