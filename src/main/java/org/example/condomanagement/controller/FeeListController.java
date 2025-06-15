package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import org.example.condomanagement.model.Fee;
import org.example.condomanagement.service.FeeService;

import java.io.IOException;

public class FeeListController {

    @FXML
    private TableView<Fee> tblFees;
    @FXML
    private TableColumn<Fee, String> colName;
    @FXML
    private TableColumn<Fee, Fee.FeeCategory> colCategory;
    @FXML
    private TableColumn<Fee, Fee.CalculationMethod> colMethod;
    @FXML
    private TableColumn<Fee, Double> colAmount;
    @FXML
    private TableColumn<Fee, Void> colActions;

    private final FeeService feeService = new FeeService();
    private final ObservableList<Fee> feeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("feeName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("feeCategory"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("calculationMethod"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("feeAmount"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");

            {
                btnEdit.getStyleClass().add("edit-button");
                btnDelete.getStyleClass().add("delete-button");

                btnEdit.setOnAction(e -> {
                    Fee fee = getTableView().getItems().get(getIndex());
                    openFeeDialog(fee);
                });

                btnDelete.setOnAction(e -> {
                    Fee fee = getTableView().getItems().get(getIndex());
                    if (showConfirm("Xóa loại phí", "Bạn có chắc muốn xóa '" + fee.getFeeName() + "' không?")) {
                        feeService.delete(fee);
                        loadFees();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tblFees.setItems(feeList);
        loadFees();
    }

    private void loadFees() {
        feeList.setAll(feeService.findAll());
    }

    private void openFeeDialog(Fee fee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_fee_dialog.fxml"));
            DialogPane pane = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle(fee == null ? "Thêm loại phí" : "Chỉnh sửa loại phí");

            CreateFeeDialogController controller = loader.getController();
            controller.setFeeService(feeService);
            controller.setEditingFee(fee);

            dialog.setResultConverter(bt -> controller.processSave() ? bt : null);
            dialog.showAndWait();
            loadFees();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở hộp thoại loại phí.");
        }
    }

    private boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    private void onAddFee() {
        openFeeDialog(null);
    }
}
