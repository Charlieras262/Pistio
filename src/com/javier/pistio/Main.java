package com.javier.pistio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("ui/main.fxml"));
        primaryStage.setTitle("Pistio");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> { // Se agrega un listener para cuando se hace la peticion de cierre de la ventana.
            System.exit(0); // Sirve para cerrar todos los hilos abiertos por la instancia actual del programa.
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
