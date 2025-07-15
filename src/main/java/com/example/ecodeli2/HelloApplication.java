package com.example.ecodeli2;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class HelloApplication extends Application {
    
    private static final String API_URL = "http://88.172.140.59:52000/api/users";
    
    @Override
    public void start(Stage stage) throws IOException {
        // Télécharger les données utilisateur au démarrage
        downloadUserData();
        
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
    
    private void downloadUserData() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Obtenir le chemin du fichier user.json dans les resources
                String userHome = System.getProperty("user.home");
                Path resourcesPath = Paths.get("src", "main", "resources", "com", "example", "ecodeli2");
                Path userJsonPath = resourcesPath.resolve("user.json");
                
                // Créer le répertoire s'il n'existe pas
                Files.createDirectories(resourcesPath);
                
                // Écrire les données dans le fichier
                Files.write(userJsonPath, response.body().getBytes());
                
                System.out.println("Données utilisateur téléchargées et sauvegardées avec succès !");
            } else {
                System.err.println("Erreur lors du téléchargement des données utilisateur. Code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du téléchargement des données utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}