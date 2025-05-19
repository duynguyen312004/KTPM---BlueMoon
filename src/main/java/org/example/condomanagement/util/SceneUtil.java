package org.example.condomanagement.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneUtil {
    public static void switchScene(String fxmlPath) {
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(Window -> Window.isShowing()).get(0);
            Parent root = FXMLLoader.load(SceneUtil.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneUtil.class.getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}