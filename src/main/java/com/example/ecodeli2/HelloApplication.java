package com.example.ecodeli2;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("dashboard.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 600);

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        HelloApplication.class.getResource("styles.css"),
                        "styles.css introuvable dans resources/com/example/ecodeli2"
                ).toExternalForm()
        );

        stage.setTitle("EcoDeli App");

        InputStream logoStream = Objects.requireNonNull(
                HelloApplication.class.getResourceAsStream("ecodeli-logo.png"),
                "ecodeli-logo.png introuvable dans resources/com/example/ecodeli2"
        );
        Image icon = new Image(logoStream);
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}