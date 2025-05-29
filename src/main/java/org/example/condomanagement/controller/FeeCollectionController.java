package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.condomanagement.model.FeeCollectionRow;
import org.example.condomanagement.service.FeeCollectionService;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FeeCollectionController {
    @FXML private TableView<FeeCollectionRow> tableFeeCollection;
    @FXML private TableColumn<FeeCollectionRow, Integer> colFeeCode;
    @FXML private TableColumn<FeeCollectionRow, String> colHouseholdCode;
    @FXML private TableColumn<FeeCollectionRow, String> colFeeName;
    @FXML private TableColumn<FeeCollectionRow, String> colBatch;
    @FXML private TableColumn<FeeCollectionRow, Double> colAmount;
    @FXML private TableColumn<FeeCollectionRow, Double> colActualAmount;
    @FXML private TableColumn<FeeCollectionRow, String> colDate;
    @FXML private ComboBox<String> cbSearchType;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit, btnDelete;

    private final FeeCollectionService service = new FeeCollectionService();
    private ObservableList<FeeCollectionRow> masterData;

    @FXML
    public void initialize() {
        colFeeCode.setCellValueFactory(new PropertyValueFactory<>("billingItemId"));
        colHouseholdCode.setCellValueFactory(new PropertyValueFactory<>("householdCode"));
        colFeeName.setCellValueFactory(new PropertyValueFactory<>("feeName"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("expectedAmount"));
        colActualAmount.setCellValueFactory(new PropertyValueFactory<>("actualAmount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        List<FeeCollectionRow> data = service.getAllFeeCollections();
        masterData = FXCollections.observableArrayList(data);
        tableFeeCollection.setItems(masterData);

        // Đặt giá trị mặc định cho ComboBox nếu chưa có
        if (cbSearchType.getItems().isEmpty()) {
            cbSearchType.getItems().addAll("Mã hộ khẩu", "Tên khoản phí", "Đợt thu");
        }
        cbSearchType.getSelectionModel().selectFirst();

        btnSearch.setOnAction(e -> handleSearch());
        btnAdd.setOnAction(e -> openCreateFeeCollection());
        btnEdit.setOnAction(e -> openEditFeeCollection());
        btnDelete.setOnAction(e -> handleDelete());
        btnEdit.disableProperty().bind(tableFeeCollection.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(tableFeeCollection.getSelectionModel().selectedItemProperty().isNull());
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        String type = cbSearchType.getValue();
        if (keyword.isEmpty()) {
            tableFeeCollection.setItems(masterData);
            return;
        }
        ObservableList<FeeCollectionRow> filtered = masterData.stream().filter(row -> {
            switch (type) {
                case "Mã hộ khẩu":
                    return row.getHouseholdCode() != null && row.getHouseholdCode().toLowerCase(Locale.ROOT).contains(keyword);
                case "Tên khoản phí":
                    return row.getFeeName() != null && row.getFeeName().toLowerCase(Locale.ROOT).contains(keyword);
                case "Đợt thu":
                    return row.getBatchName() != null && row.getBatchName().toLowerCase(Locale.ROOT).contains(keyword);
                default:
                    return false;
            }
        }).collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableFeeCollection.setItems(filtered);
    }

    private void openCreateFeeCollection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_fee_collection.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm khoản thu mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Sau khi dialog đóng, reload lại dữ liệu
            boolean changed = reloadTableData();
            if (changed) showQuickAlert("Đã thêm khoản thu mới!");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được form thêm khoản thu!").show();
        }
    }

    private void openEditFeeCollection() {
        FeeCollectionRow selected = tableFeeCollection.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showQuickAlert("Vui lòng chọn khoản thu để sửa!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_fee_collection.fxml"));
            Parent root = loader.load();
            CreateFeeCollectionController ctrl = loader.getController();
            ctrl.setEditMode(selected); // Truyền dữ liệu khoản thu cần sửa
            Stage stage = new Stage();
            stage.setTitle("Sửa khoản thu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            boolean changed = reloadTableData();
            if (changed) showQuickAlert("Đã cập nhật khoản thu!");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được form sửa khoản thu!").show();
        }
    }

    private void handleDelete() {
        FeeCollectionRow selected = tableFeeCollection.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showQuickAlert("Vui lòng chọn khoản thu để xóa!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa khoản thu này?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                try {
                    org.example.condomanagement.dao.BillingItemDao dao = new org.example.condomanagement.dao.BillingItemDao();
                    org.example.condomanagement.model.BillingItem item = dao.findById(selected.getBillingItemId());
                    dao.delete(item);
                    boolean changed = reloadTableData();
                    if (changed) showQuickAlert("Đã xóa khoản thu!");
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa khoản thu!").show();
                }
            }
        });
    }

    /**
     * Reload lại bảng, trả về true nếu có thêm khoản thu mới.
     */
    private boolean reloadTableData() {
        int oldSize = masterData.size();
        List<FeeCollectionRow> data = service.getAllFeeCollections();
        masterData.setAll(data);
        tableFeeCollection.setItems(masterData);
        return masterData.size() > oldSize;
    }

    /**
     * Hiển thị thông báo ngắn gọn (snackbar/toast) khi thêm thành công.
     */
    private void showQuickAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initOwner(tableFeeCollection.getScene().getWindow());
        alert.show();
        // Tự động tắt sau 1.5s
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(alert::close);
        }).start();
    }
}
