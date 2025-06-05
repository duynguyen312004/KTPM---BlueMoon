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
import org.hibernate.Session;
import org.example.condomanagement.config.HibernateUtil;

import java.util.List;

public class CreateFeeCollectionController {
    @FXML private ComboBox<Household> cbHousehold;
    @FXML private ComboBox<Fee> cbFeeName;
    @FXML private ComboBox<CollectionBatch> cbBatch;
    @FXML private TextField txtAmount;
    @FXML private TextField txtActualAmount;
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

        // Load danh sách khoản thu
        List<Fee> fees = feeDao.findAll();
        cbFeeName.setItems(FXCollections.observableArrayList(fees));
        cbFeeName.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Fee f) { return f == null ? "" : f.getFeeName(); }
            @Override public Fee fromString(String s) { return null; }
        });

        // Khi chọn khoản thu, tự động điền số tiền
        cbFeeName.setOnAction(e -> {
            Fee selectedFee = cbFeeName.getValue();
            if (selectedFee != null) {
                txtAmount.setText(String.valueOf(selectedFee.getFeeAmount()));
                txtAmount.setDisable(true); // Không cho phép sửa số tiền
            }
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
            cbFeeName.setValue(editingItem.getFee());
            cbBatch.setValue(editingItem.getBatch());
            txtAmount.setText(editingItem.getExpectedAmount().toString());
            txtActualAmount.setText(editingItem.getActualAmount().toString());
            
            // Kiểm tra xem khoản thu đã có actual_amount chưa
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                String hql = "SELECT COUNT(t) FROM Transaction t WHERE t.billingItem.fee.feeId = :feeId";
                Long count = session.createQuery(hql, Long.class)
                        .setParameter("feeId", editingItem.getFee().getFeeId())
                        .uniqueResult();
                
                // Nếu đã có giao dịch, không cho phép sửa số tiền
                txtAmount.setDisable(count != null && count > 0);
            }
        }
    }

    private void handleSave() {
        try {
            Household household = cbHousehold.getValue();
            Fee fee = cbFeeName.getValue();
            CollectionBatch batch = cbBatch.getValue();
            double expectedAmount = Double.parseDouble(txtAmount.getText().trim());
            double actualAmount = Double.parseDouble(txtActualAmount.getText().trim());

            if (household == null || fee == null || batch == null) {
                showAlert("Vui lòng nhập đầy đủ thông tin!");
                return;
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