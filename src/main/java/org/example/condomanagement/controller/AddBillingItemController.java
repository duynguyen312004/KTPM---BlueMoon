package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
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
            if(billingItemToEdit!=null){
                billingItemToEdit.setBatch(selectedBatch);
                billingItemToEdit.setFee(selectedFee);
                billingItemToEdit.setHousehold(selectedHousehold);
                billingItemDao.update(billingItemToEdit);
                billingItemTable.refresh();
            }
            else{
                double expected_amount=0;

                if(selectedFee.getFeeId()==2){
                    expected_amount = selectedFee.getFeeAmount()*vehicleDao.countMotorbikesByHouseholdId(selectedHousehold.getHouseholdId());
                }
                else if(selectedFee.getFeeId()==3){
                    expected_amount = selectedFee.getFeeAmount()*vehicleDao.countCarsByHouseholdId(selectedHousehold.getHouseholdId());
                }
                else if(selectedFee.getFeeId()==4){
                    expected_amount = selectedFee.getFeeAmount()*householdDao.getAreaByHouseholdId(selectedHousehold.getHouseholdId());
                }
                else expected_amount = selectedFee.getFeeAmount();
                BillingItem billingItem = new BillingItem(selectedBatch, selectedFee, selectedHousehold, 0.0, expected_amount, BillingItem.Status.Pending);
                billingItem.setUpdatedAt(LocalDateTime.now());
                BillingItem saved = billingItemDao.save(billingItem);
                billingItemTable.getItems().add(saved);
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khoản thu thành công.");
            closeWindow();
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

        // Gán dữ liệu vào các trường giao diện
        collectionBatchComboBox.setValue(billingItem.getBatch());
        feeComboBox.setValue(billingItem.getFee());
        householdComboBox.setValue(billingItem.getHousehold());
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
