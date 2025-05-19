package org.example.condomanagement.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.condomanagement.util.Constants;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load Login FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Create Scene
        Scene scene = new Scene(root);

        // Apply global stylesheet
        scene.getStylesheets().add(
                getClass().getResource("/css/app.css").toExternalForm());

        // Configure Stage
        stage.setTitle(Constants.APP_TITLE);

        // Set application icon
        Image logo = new Image(getClass().getResourceAsStream("/assets/logo.png"));
        stage.getIcons().add(logo);

        // Optional: fix window size or maximize
        stage.setResizable(false);

        // Show UI
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
