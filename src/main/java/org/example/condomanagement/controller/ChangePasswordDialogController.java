package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.condomanagement.service.UserService;

public class ChangePasswordDialogController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;

    private UserService userService;
    private String username;

    public void setUserService(UserService service) {
        this.userService = service;
    }

    public void setUsername(String username) {
        this.username = username;
        txtUsername.setText(username);
    }

    public boolean processChange() {
        String newPass = txtNewPassword.getText();
        String confirm = txtConfirmPassword.getText();

        if (newPass.isEmpty() || confirm.isEmpty()) {
            showWarning("Vui lòng nhập đầy đủ mật khẩu.");
            return false;
        }

        if (!newPass.equals(confirm)) {
            showError("Mật khẩu xác nhận không khớp.");
            return false;
        }

        boolean ok = userService.resetPassword(username, newPass);
        if (ok) {
            showInfo("Đổi mật khẩu thành công!");
        } else {
            showError("Đổi mật khẩu thất bại. Vui lòng thử lại.");
        }

        return ok;
    }

    // ==== ALERT HELPERS ====

    private void showInfo(String msg) {
        showAlert(Alert.AlertType.INFORMATION, msg);
    }

    private void showWarning(String msg) {
        showAlert(Alert.AlertType.WARNING, msg);
    }

    private void showError(String msg) {
        showAlert(Alert.AlertType.ERROR, msg);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
