package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.dao.BillingItemDao;
import org.example.condomanagement.dao.CollectionBatchDao;
import org.example.condomanagement.dao.FeeDao;
import org.example.condomanagement.dao.HouseholdDao;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.CollectionBatch;
import org.example.condomanagement.model.Fee;
import org.example.condomanagement.model.Household;

import java.time.LocalDate;
import java.util.List;

public class CreateFeeCollectionController {
    @FXML private ComboBox<Household> cbHousehold;
    @FXML private TextField txtFeeName;
    @FXML private ComboBox<Fee.FeeCategory> cbFeeCategory;
    @FXML private ComboBox<CollectionBatch> cbBatch;
    @FXML private TextField txtAmount;
    @FXML private TextField txtActualAmount;
    @FXML private DatePicker dpDate;
    @FXML private Button btnCancel, btnSave;

    private final HouseholdDao householdDao = new HouseholdDao();
    private final CollectionBatchDao batchDao = new CollectionBatchDao();
    private final FeeDao feeDao = new FeeDao();
    private final BillingItemDao billingItemDao = new BillingItemDao();

    private org.example.condomanagement.model.BillingItem editingItem = null;

    @FXML
    public void initialize() {
        // Load danh sách mã hộ khẩu
        List<Household> households = householdDao.findAll();
        cbHousehold.setItems(FXCollections.observableArrayList(households));
        cbHousehold.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Household h) { return h == null ? "" : h.getApartmentCode(); }
            @Override public Household fromString(String s) { return null; }
        });

        // Load danh sách loại phí
        cbFeeCategory.setItems(FXCollections.observableArrayList(Fee.FeeCategory.values()));
        cbFeeCategory.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Fee.FeeCategory c) { 
                return c == null ? "" : switch(c) {
                    case Service -> "Dịch vụ";
                    case Management -> "Quản lý";
                    case Parking -> "Gửi xe";
                    case Utility -> "Tiện ích";
                    case Voluntary -> "Tự nguyện";
                };
            }
            @Override public Fee.FeeCategory fromString(String s) { return null; }
        });

        // Load danh sách đợt thu
        List<CollectionBatch> batches = batchDao.findAll();
        cbBatch.setItems(FXCollections.observableArrayList(batches));
        cbBatch.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(CollectionBatch b) { return b == null ? "" : b.getName(); }
            @Override public CollectionBatch fromString(String s) { return null; }
        });

        btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());
        btnSave.setOnAction(e -> handleSave());
    }

    public void setEditMode(org.example.condomanagement.model.FeeCollectionRow row) {
        editingItem = new BillingItemDao().findById(row.getBillingItemId());
        if (editingItem != null) {
            cbHousehold.setValue(editingItem.getHousehold());
            txtFeeName.setText(editingItem.getFee().getFeeName());
            cbFeeCategory.setValue(editingItem.getFee().getFeeCategory());
            cbBatch.setValue(editingItem.getBatch());
            txtAmount.setText(editingItem.getExpectedAmount().toString());
            txtActualAmount.setText(editingItem.getActualAmount().toString());
        }
    }

    private void handleSave() {
        try {
            Household household = cbHousehold.getValue();
            String feeName = txtFeeName.getText().trim();
            Fee.FeeCategory feeCategory = cbFeeCategory.getValue();
            CollectionBatch batch = cbBatch.getValue();
            double expectedAmount = Double.parseDouble(txtAmount.getText().trim());
            double actualAmount = Double.parseDouble(txtActualAmount.getText().trim());
            LocalDate date = dpDate.getValue();

            if (household == null || feeName.isEmpty() || feeCategory == null || batch == null || date == null) {
                showAlert("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            Fee fee = feeDao.findAll().stream().filter(f -> f.getFeeName().equalsIgnoreCase(feeName)).findFirst().orElse(null);
            if (fee == null) {
                fee = new Fee();
                fee.setFeeName(feeName);
                fee.setFeeCategory(feeCategory);
                fee.setFeeAmount(expectedAmount);
                fee.setCalculationMethod(Fee.CalculationMethod.Fixed);
                fee = feeDao.save(fee);
            }

            if (editingItem != null) {
                editingItem.setHousehold(household);
                editingItem.setFee(fee);
                editingItem.setBatch(batch);
                editingItem.setExpectedAmount(expectedAmount);
                editingItem.setActualAmount(actualAmount);
                editingItem.setStatus(actualAmount >= expectedAmount ? BillingItem.Status.Paid : BillingItem.Status.Pending);
                new BillingItemDao().save(editingItem);
                showAlert("Đã cập nhật khoản thu!", Alert.AlertType.INFORMATION);
            } else {
                BillingItem item = new BillingItem();
                item.setHousehold(household);
                item.setFee(fee);
                item.setBatch(batch);
                item.setExpectedAmount(expectedAmount);
                item.setActualAmount(actualAmount);
                item.setStatus(actualAmount >= expectedAmount ? BillingItem.Status.Paid : BillingItem.Status.Pending);
                billingItemDao.save(item);
                showAlert("Đã thêm khoản thu thành công!", Alert.AlertType.INFORMATION);
            }
            ((Stage) btnSave.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu khoản thu: " + e.getMessage());
        }
    }

    private void showAlert(String msg) { showAlert(msg, Alert.AlertType.WARNING); }
    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.INFORMATION ? "Thành công" : "Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
} 