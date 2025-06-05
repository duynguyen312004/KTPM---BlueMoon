package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.condomanagement.model.User;
import org.example.condomanagement.service.UserService;

public class CreateAccountantDialogController {

    private UserService userService;

    public void setUserService(@SuppressWarnings("exports") UserService userService) {
        this.userService = userService;
    }

    /**
     * Được gọi bởi AdminDashboardController sau khi nhấn OK
     */
    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    public boolean processCreate() {
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (fullName.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ các trường.");
            return false;
        }

        if (!phone.matches("^\\d{9,11}$")) {
            showAlert(Alert.AlertType.WARNING, "Số điện thoại không hợp lệ", "Vui lòng nhập số từ 9–11 chữ số.");
            return false;
        }

        boolean success = userService.createStaff(username, password, User.Role.Accountant, fullName, phone);
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Tạo tài khoản thất bại", "Username đã tồn tại.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tài khoản kế toán đã được tạo.");
        }
        return success;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
