package org.example.condomanagement.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.example.condomanagement.model.User;
import org.example.condomanagement.service.UserService;

/**
 * Controller for the Login screen with role-based routing.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private javafx.scene.image.ImageView logoView;

    @FXML
    private StackPane mainPane;

    @FXML
    private VBox loadingOverlay;

    @FXML
    private Button btnLogin;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/assets/logo.png"));
            logoView.setImage(logo);
        } catch (Exception e) {
            System.err.println("⚠️ Không thể nạp logo: " + e.getMessage());
        }

        System.out.println(getClass().getResource("/css/app.css"));

        // Load CSS nếu scene đã sẵn sàng
        logoView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            }
        });

    }

    /**
     * Called when the user clicks the "Login" button.
     * Routes to different dashboards based on user role.
     */
    @FXML
    private void onLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", null, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        loadingOverlay.setVisible(true);
        btnLogin.setDisable(true);

        Task<User> task = new Task<>() {
            @Override
            protected User call() throws Exception {
                Thread.sleep(100); // giả lập loading
                return userService.authenticate(username, password);
            }

            @Override
            protected void succeeded() {
                User user = getValue();
                loadingOverlay.setVisible(false);
                btnLogin.setDisable(false);

                if (user != null) {
                    try {
                        FXMLLoader loader;
                        Parent root;
                        Stage stage = (Stage) btnLogin.getScene().getWindow();

                        switch (user.getRole()) {
                            case Admin -> {
                                loader = new FXMLLoader(getClass().getResource("/fxml/admin_dashboard.fxml"));
                                root = loader.load();
                                AdminDashboardController ctrl = loader.getController();
                                ctrl.initData(user);
                                stage.setScene(new Scene(root));
                                stage.centerOnScreen();
                                stage.setMaximized(true);
                            }
                            case Accountant -> {
                                loader = new FXMLLoader(getClass().getResource("/fxml/accountant_dashboard.fxml"));
                                root = loader.load();
                                AccountantDashboardController ctrl = loader.getController();
                                ctrl.initData(user);
                                stage.setScene(new Scene(root, 1300, 800));
                                stage.centerOnScreen();
                                stage.setResizable(true);
                            }
                            default -> throw new IllegalStateException("Unknown role");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Không thể mở dashboard.");
                    }
                } else {
                    // Kiểm tra lý do thất bại
                    User rawUser = userService.findByUsername(username);
                    if (rawUser != null && !rawUser.getIsActive()) {
                        showAlert(Alert.AlertType.WARNING, "Tài khoản bị khóa", null,
                                "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", null,
                                "Tên đăng nhập hoặc mật khẩu không đúng.");
                    }
                }
            }

            @Override
            protected void failed() {
                loadingOverlay.setVisible(false);
                btnLogin.setDisable(false);
                showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Đã có lỗi xảy ra.");
            }
        };

        new Thread(task).start();
    }

    /**
     * Called when the user clicks the "Đăng ký" link.
     */
    @FXML
    private void onRegister(ActionEvent event) {
        // For now only admin/root can register, redirect accordingly or disable
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", null, "Vui lòng liên hệ Admin để tạo tài khoản.");
    }

    /**
     * Utility to show alert dialogs.
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}