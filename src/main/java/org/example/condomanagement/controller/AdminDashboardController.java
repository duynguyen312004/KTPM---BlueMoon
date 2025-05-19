package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.condomanagement.model.User;
import org.example.condomanagement.service.UserService;
import org.example.condomanagement.util.SceneUtil;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminDashboardController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnOpenCreatePopup;
    @FXML
    private VBox dashboardContent;
    @FXML
    private TableView<User> tblAccountants;

    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colPassword;
    @FXML
    private TableColumn<User, String> colFullName;
    @FXML
    private TableColumn<User, String> colPhone;
    @FXML
    private TableColumn<User, User.Role> colRole;
    @FXML
    private TableColumn<User, Boolean> colActive;
    @FXML
    private TableColumn<User, LocalDateTime> colCreatedAt;
    @FXML
    private TableColumn<User, Void> colActions;

    private final UserService userService = new UserService();
    private final ObservableList<User> accountants = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadLogoutIcon();
        setupTable();
        tblAccountants.setItems(accountants);
    }

    public void initData(User user) {
        lblWelcome.setText("Xin chào, " + user.getFullName());

        dashboardContent.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            }
        });

        loadAccountants();
    }

    private void loadLogoutIcon() {
        InputStream is = getClass().getResourceAsStream("/assets/logout.png");
        if (is != null) {
            ImageView iv = new ImageView(new Image(is));
            iv.setFitWidth(16);
            iv.setFitHeight(16);
            btnLogout.setGraphic(iv); // 👈 Gán icon vào nút
            btnLogout.setContentDisplay(ContentDisplay.LEFT);
            btnLogout.setGraphicTextGap(10);
            btnLogout.setText("Đăng xuất");
        } else {
            System.err.println("⚠️ Không tìm thấy logout.png!");
        }
    }

    private void setupTable() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        colActive.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(active ? "Đang hoạt động" : "Đã vô hiệu");
                    setStyle(active ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });

        colCreatedAt.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggle = new Button();
            private final Button btnReset = new Button("Đổi mật khẩu");

            {
                btnReset.getStyleClass().add("btn-success");
                btnToggle.getStyleClass().add("btn-danger");

                btnReset.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    openChangePasswordDialog(u);
                });

                btnToggle.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    boolean deactivating = u.getIsActive();
                    String actionText = deactivating ? "Vô hiệu hóa" : "Kích hoạt lại";

                    boolean confirmed = showConfirm(
                            actionText,
                            actionText + " tài khoản '" + u.getUsername() + "'?");

                    if (confirmed) {
                        u.setIsActive(!u.getIsActive());
                        userService.updateUser(u);
                        loadAccountants();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User u = getTableView().getItems().get(getIndex());
                    btnToggle.setText(u.getIsActive() ? "Vô hiệu hóa" : "Kích hoạt lại");
                    btnToggle.getStyleClass().setAll(u.getIsActive() ? "btn-danger" : "btn-success");

                    HBox actionBox = new HBox(10, btnReset, btnToggle);
                    actionBox.setAlignment(Pos.CENTER);

                    StackPane container = new StackPane(actionBox);
                    container.setAlignment(Pos.CENTER);

                    setGraphic(container);
                }
            }
        });

        tblAccountants.widthProperty().addListener((obs, oldVal, newVal) -> {
            double total = newVal.doubleValue();
            colUsername.setPrefWidth(total * 0.12);
            colPassword.setPrefWidth(total * 0.12);
            colFullName.setPrefWidth(total * 0.14);
            colPhone.setPrefWidth(total * 0.12);
            colRole.setPrefWidth(total * 0.08);
            colActive.setPrefWidth(total * 0.10);
            colCreatedAt.setPrefWidth(total * 0.14);
            colActions.setPrefWidth(total * 0.18);
        });
    }

    private void openChangePasswordDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change_password_dialog.fxml"));
            DialogPane pane = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Đổi mật khẩu");

            ChangePasswordDialogController ctrl = loader.getController();
            ctrl.setUserService(userService);
            ctrl.setUsername(user.getUsername());

            dialog.setResultConverter(bt -> {
                if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return ctrl.processChange() ? bt : null;
                }
                return bt;
            });

            dialog.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Không thể mở hộp thoại đổi mật khẩu.");
        }
    }

    @FXML
    private void onReloadAccountants() {
        loadAccountants();
    }

    private void loadAccountants() {
        accountants.setAll(userService.getAllByRole(User.Role.Accountant));
    }

    @FXML
    private void onOpenCreatePopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_accountant_dialog.fxml"));
            DialogPane pane = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Thêm tài khoản kế toán");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

            CreateAccountantDialogController ctrl = loader.getController();
            ctrl.setUserService(userService);

            dialog.setResultConverter(bt -> {
                if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return ctrl.processCreate() ? bt : null;
                }
                return bt;
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                loadAccountants();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        boolean confirmed = showConfirm("Đăng xuất", "Bạn có chắc muốn đăng xuất?");
        if (confirmed) {
            SceneUtil.switchScene("/fxml/login.fxml");
        }
    }

    private boolean showConfirm(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType t, String title, String header, String content) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
