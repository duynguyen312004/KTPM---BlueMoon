package org.example.condomanagement.controller;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.condomanagement.dao.BillingItemDao;
import org.example.condomanagement.dao.CollectionBatchDao;
import org.example.condomanagement.dao.FeeDao;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.CollectionBatch;
import org.example.condomanagement.model.Fee;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class FeeManagementController {
    @FXML
    private TableView<BillingItem> billingItemTableView;
    @FXML
    private TableColumn<BillingItem, Integer> billingItemIdColumn;
    @FXML
    private TableColumn<BillingItem, String> batchNameColumn;
    @FXML
    private TableColumn<BillingItem, String> feeNameColumn;
    @FXML
    private TableColumn<BillingItem, Double> expectedAmountColumn;
    @FXML
    private TableColumn<BillingItem, Double> actualAmountColumn;
    @FXML
    private TableColumn<BillingItem, String> householdCodeColumn;

    @FXML
    private ComboBox<String> dotthuComboBox;

    @FXML
    private TextField searchFeeTextField;

    private final CollectionBatchDao collectionBatchDao = new CollectionBatchDao();
    private final BillingItemDao billingItemDao = new BillingItemDao();

    CollectionBatch placeholder = new CollectionBatch();

    @FXML
    public void initialize() {
        // Load danh sách đợt thu
        List<CollectionBatch> batches = collectionBatchDao.findAll();
        List<String> batchNames = batches.stream()
                .map(CollectionBatch::getName)
                .toList();

        placeholder.setBatchId(-1); // ID giả
        placeholder.setName("Chọn đợt thu"); // Tên hiển thị
        batches.add(0, placeholder);
        dotthuComboBox.setItems(FXCollections.observableArrayList(batchNames));
        dotthuComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            searchFeeButton(null); // Gọi lại hàm lọc hiện có
        });


        // Thiết lập bảng billing item
        billingItemIdColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getBillingItemId())
        );

        batchNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getBatch().getName())
        );

        feeNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getFee().getFeeName())
        );

        expectedAmountColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getExpectedAmount())
        );

        actualAmountColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getActualAmount())
        );

        householdCodeColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getHousehold().getApartmentCode())
        );


        refreshBillingItems();
    }

    private void refreshBillingItems() {
        List<BillingItem> items = billingItemDao.findAll();
        billingItemTableView.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    public void addCollectionBatchButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_collection_batch.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thêm đợt thu");
            stage.showAndWait();

            // Reload ComboBox sau khi thêm đợt thu
            List<String> batchNames = collectionBatchDao.findAll().stream()
                    .map(CollectionBatch::getName)
                    .toList();
            dotthuComboBox.setItems(FXCollections.observableArrayList(batchNames));
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể mở giao diện thêm đợt thu: " + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    public void searchFeeButton(javafx.event.ActionEvent actionEvent) {
        String selectedName = dotthuComboBox.getValue();
        String keyword = searchFeeTextField.getText().toLowerCase().trim();

        List<BillingItem> allFees = billingItemDao.findAll();
        List<BillingItem> filtered = allFees.stream()
                .filter(billingItem -> {
                    boolean matchName = selectedName.equals("Tất cả")
                            || billingItem.getBatch().getName().equals(selectedName);
                    boolean matchKeyword = keyword.isEmpty() || billingItem.getFee().getFeeName().toLowerCase().contains(keyword);
                    return matchName && matchKeyword;
                })
                .toList();

        billingItemTableView.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void addFeeButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_fee.fxml"));
            Parent root = loader.load();

            // Truyền observable list hiện tại cho controller
            AddBillingItemController controller = loader.getController();
            controller.setBillingItemTable(billingItemTableView); // Truyền danh sách đang hiển thị

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thêm khoản phí");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateFeeButton(ActionEvent actionEvent) {
       BillingItem billingItem = billingItemTableView.getSelectionModel().getSelectedItem();

        if (billingItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cập nhật khoản phí");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một khoản phí để sửa.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_fee.fxml"));
            Parent root = loader.load();

            // ✅ Lấy controller
            AddBillingItemController controller = loader.getController();

            // ✅ Truyền fee được chọn
            controller.setBillingItemToEdit(billingItem);

            // ✅ Truyền cả feeTableView (QUAN TRỌNG!)
            controller.setBillingItemTable(billingItemTableView); // <-- bạn đang thiếu dòng này

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cập nhật khoản phí");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteFeeButton(ActionEvent actionEvent) {
        // Lấy khoản phí được chọn
        BillingItem selectedBillingitem = billingItemTableView.getSelectionModel().getSelectedItem();

        if (selectedBillingitem == null) {
            // Không chọn gì
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Xóa khoản phí");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một khoản phí để xóa.");
            alert.showAndWait();
            return;
        }

        // Xác nhận xóa
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa khoản phí \"" + selectedBillingitem.getFee().getFeeName() + "\"?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                billingItemDao.delete(selectedBillingitem); // Gọi DAO để xóa trong DB
                billingItemTableView.getItems().remove(selectedBillingitem); // Xóa khỏi giao diện

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Xóa thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã xóa khoản phí thành công.");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi khi xóa");
                alert.setHeaderText("Không thể xóa khoản phí");
                alert.setContentText("Chi tiết lỗi: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void resetButton(ActionEvent actionEvent) {
        dotthuComboBox.setValue(placeholder.getName());
        searchFeeTextField.clear();
        billingItemTableView.setItems(FXCollections.observableArrayList(billingItemDao.findAll()));
    }

}
