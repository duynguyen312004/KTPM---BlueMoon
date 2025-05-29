package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.example.condomanagement.model.User;
import org.example.condomanagement.service.TransactionService;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AccountantDashboardController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label lblWelcome, lblHeader;
    @FXML
    private Button btnHome, btnResident, btnHousehold, btnFee, btnCollection, btnReports;
    @FXML
    private Button btnAdd, btnLogout;
    @FXML
    private StackPane contentPane;

    @SuppressWarnings("unused")
    private final TransactionService txService = new TransactionService();
    private List<Button> navButtons;

    @FXML
    public void initialize() {
        // 1) load CSS
        String css = getClass().getResource("/css/app.css").toExternalForm();
        rootPane.getStylesheets().add(css);

        // 2) gom tất cả nav-buttons vào list để highlight
        navButtons = Arrays.asList(
                btnHome, btnResident, btnHousehold, btnFee, btnCollection, btnReports);

        // 3) setup từng nút nav: icon + action
        setupNav(btnHome, "/fxml/home.fxml", "Trang chủ", "", false, "home.png");
        setupNav(btnResident, "/fxml/resident_list.fxml", "Nhân khẩu", "", false, "resident.png");
        setupNav(btnHousehold, "/fxml/household_list.fxml", "Hộ khẩu", "", false, "household.png");
        setupNav(btnFee, "/fxml/fee_management.fxml", "Khoản thu", "", false, "fee.png");
        setupNav(btnCollection, "/fxml/fee_collection_management.fxml", "Thu phí", "Thêm thu phí", true, "collection.png");
        setupNav(btnReports, "/fxml/reporting.fxml", "Báo cáo", "", false, "reports.png");

        // 4) logout button
        btnLogout.setGraphic(makeIcon("logout.png"));
        btnLogout.setContentDisplay(ContentDisplay.LEFT);
        btnLogout.setGraphicTextGap(10);
        btnLogout.setText("Đăng xuất");

        // 5) show home mặc định
        btnHome.fire();
    }

    private void setupNav(Button btn,
            String fxmlPath,
            String headerText,
            String addBtnText,
            boolean showAdd,
            String iconFile) {
        // icon + text
        btn.setGraphic(makeIcon(iconFile));
        btn.setContentDisplay(ContentDisplay.LEFT);

        // hành động khi click
        btn.setOnAction(e -> {
            lblHeader.setText(headerText);
            btnAdd.setVisible(showAdd);
            if (showAdd)
                btnAdd.setText(addBtnText);

            loadContent(fxmlPath);
            highlightActive(btn);
        });
    }

    private void loadContent(String fxmlPath) {
        try {
            System.out.println("Loading FXML: " + fxmlPath);
            contentPane.getChildren().setAll(
                    (javafx.scene.Node) FXMLLoader.load(getClass().getResource(fxmlPath)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private ImageView makeIcon(String fileName) {
        String path = "/assets/" + fileName;
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("❌ Asset not found: " + path);
            return new ImageView();
        }
        Image img = new Image(is);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(16);
        iv.setFitHeight(16);
        return iv;
    }

    /** Gán class "active" cho nút đang chọn, bỏ class ở các nút khác */
    private void highlightActive(Button activeBtn) {
        navButtons.forEach(btn -> {
            if (btn == activeBtn) {
                if (!btn.getStyleClass().contains("active"))
                    btn.getStyleClass().add("active");
            } else {
                btn.getStyleClass().remove("active");
            }
        });
    }

    /** Được gọi từ LoginController */
    public void initData(User user) {
        lblWelcome.setText(user.getFullName());
    }

    @FXML
    private void onLogout() {
        // Tạo hộp thoại xác nhận
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận đăng xuất");
        confirm.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        confirm.setContentText("Hành động này sẽ đưa bạn về màn hình đăng nhập.");

        // Gán nút Yes/No rõ ràng hơn
        ButtonType yesBtn = new ButtonType("Đăng xuất", ButtonBar.ButtonData.OK_DONE);
        ButtonType noBtn = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yesBtn, noBtn);

        // Hiển thị dialog và xử lý kết quả
        confirm.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                try {

                    // Load lại login.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Parent loginRoot = loader.load();

                    // Chuyển scene về màn hình login
                    Stage stage = (Stage) btnLogout.getScene().getWindow();
                    stage.setScene(new Scene(loginRoot));
                    stage.centerOnScreen();
                    stage.show();

                    System.out.println("Người dùng đã đăng xuất.");

                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Đã xảy ra lỗi khi đăng xuất.");
                }
            }
        });
    }

    // Hàm báo lỗi nếu cần
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
