package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.condomanagement.dao.*;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.CollectionBatch;
import org.example.condomanagement.model.Fee;
import org.example.condomanagement.model.Household;

import java.time.LocalDateTime;

public class AddBillingItemController {

    private BillingItem billingItemToEdit;

    private TableView<BillingItem> billingItemTable;

    @FXML
    private ComboBox<CollectionBatch> collectionBatchComboBox;

    @FXML
    private ComboBox<Fee> feeComboBox;

    @FXML
    private ComboBox<Household> householdComboBox;

    @FXML
    private TextField amountTextField;

    CollectionBatchDao collectionBatchDao = new CollectionBatchDao();
    FeeDao feeDao = new FeeDao();
    HouseholdDao householdDao = new HouseholdDao();
    BillingItemDao billingItemDao = new BillingItemDao();
    VehicleDao vehicleDao = new VehicleDao();

    @FXML
    public void initialize() {
        // Load dữ liệu vào ComboBox
        collectionBatchComboBox.setItems(FXCollections.observableArrayList(collectionBatchDao.findAll()));
        feeComboBox.setItems(FXCollections.observableArrayList(feeDao.findAll()));
        householdComboBox.setItems(FXCollections.observableArrayList(householdDao.findAll()));

        // Listener: tự động cập nhật amount khi đã chọn đủ 3 trường
        collectionBatchComboBox.setOnAction(e -> updateExpectedAmount());
        feeComboBox.setOnAction(e -> updateExpectedAmount());
        householdComboBox.setOnAction(e -> updateExpectedAmount());
    }

    private void updateExpectedAmount() {
        CollectionBatch selectedBatch = collectionBatchComboBox.getValue();
        Fee selectedFee = feeComboBox.getValue();
        Household selectedHousehold = householdComboBox.getValue();

        if (selectedBatch == null || selectedFee == null || selectedHousehold == null) return;

        // Nếu là loại phí nhập tay (id == 4), không tính tự động
        if (selectedFee.getFeeId() == 4) {
            amountTextField.setText("0");
            return;

        }

        double expectedAmount = 0.0;

        if (selectedFee.getFeeId() == 2) {
            expectedAmount = selectedFee.getFeeAmount() *
                    vehicleDao.countMotorbikesByHouseholdId(selectedHousehold.getHouseholdId());
        } else if (selectedFee.getFeeId() == 3) {
            expectedAmount = selectedFee.getFeeAmount() *
                    vehicleDao.countCarsByHouseholdId(selectedHousehold.getHouseholdId());
        } else if (selectedFee.getFeeId() == 1) {
            expectedAmount = selectedFee.getFeeAmount() *
                    householdDao.getAreaByHouseholdId(selectedHousehold.getHouseholdId());
        } else {
            expectedAmount = selectedFee.getFeeAmount();
        }

        amountTextField.setText(String.valueOf(expectedAmount));
    }

    @FXML
    public void handleSave() {
        CollectionBatch selectedBatch = collectionBatchComboBox.getValue();
        Fee selectedFee = feeComboBox.getValue();
        Household selectedHousehold = householdComboBox.getValue();

        if (selectedBatch == null || selectedFee == null || selectedHousehold == null) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng chọn đầy đủ đợt thu, khoản phí và hộ khẩu.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountTextField.getText().trim());

            if (billingItemToEdit != null) {
                billingItemToEdit.setBatch(selectedBatch);
                billingItemToEdit.setFee(selectedFee);
                billingItemToEdit.setHousehold(selectedHousehold);
                billingItemToEdit.setExpectedAmount(amount);
                billingItemDao.update(billingItemToEdit);
                billingItemTable.refresh();
            } else {
                BillingItem billingItem = new BillingItem(
                        selectedBatch,
                        selectedFee,
                        selectedHousehold,
                        0.0,
                        amount,
                        BillingItem.Status.Pending
                );
                billingItem.setUpdatedAt(LocalDateTime.now());
                BillingItem saved = billingItemDao.save(billingItem);
                billingItemTable.getItems().add(saved);
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khoản thu thành công.");
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Số tiền không hợp lệ. Vui lòng nhập đúng định dạng số.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm khoản thu:\n" + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    public void setBillingItemTable(TableView<BillingItem> billingItemTable) {
        this.billingItemTable = billingItemTable;
    }

    public void setBillingItemToEdit(BillingItem billingItem) {
        this.billingItemToEdit = billingItem;

        collectionBatchComboBox.setValue(billingItem.getBatch());
        feeComboBox.setValue(billingItem.getFee());
        householdComboBox.setValue(billingItem.getHousehold());
        amountTextField.setText(billingItem.getExpectedAmount().toString());
    }

    private void closeWindow() {
        Stage stage = (Stage) collectionBatchComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
