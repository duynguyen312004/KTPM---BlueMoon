package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.model.Fee;
import org.example.condomanagement.service.FeeService;

public class CreateFeeDialogController {

    @FXML
    private TextField txtFeeName;

    @FXML
    private ComboBox<Fee.FeeCategory> cmbCategory;

    @FXML
    private ComboBox<Fee.CalculationMethod> cmbMethod;

    @FXML
    private TextField txtAmount;

    @FXML
    private Button btnCancel;

    private Fee feeResult;
    private FeeService feeService;

    @FXML
    public void initialize() {
        cmbCategory.setItems(FXCollections.observableArrayList(Fee.FeeCategory.values()));
        cmbMethod.setItems(FXCollections.observableArrayList(Fee.CalculationMethod.values()));
    }

    public void setFeeService(FeeService feeService) {
        this.feeService = feeService;
    }

    public boolean processSave() {
        if (!validateInputs())
            return false;

        if (feeResult == null) {
            feeResult = new Fee();
        }

        feeResult.setFeeName(txtFeeName.getText().trim());
        feeResult.setFeeCategory(cmbCategory.getValue());
        feeResult.setCalculationMethod(cmbMethod.getValue());
        feeResult.setFeeAmount(Double.parseDouble(txtAmount.getText().trim()));

        boolean isDuplicate = feeService.existsByName(feeResult.getFeeName(), feeResult.getFeeId());
        if (isDuplicate) {
            showAlert("Tên loại phí đã tồn tại.");
            return false;
        }

        try {
            feeService.saveOrUpdate(feeResult);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể lưu loại phí. Vui lòng thử lại.");
            return false;
        }
    }

    @FXML
    private void onCancel() {
        feeResult = null;
        closeDialog();
    }

    private boolean validateInputs() {
        if (txtFeeName.getText().trim().isEmpty() ||
                cmbCategory.getValue() == null ||
                cmbMethod.getValue() == null ||
                txtAmount.getText().trim().isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin.");
            return false;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            if (amount < 0) {
                showAlert("Mức phí không được âm.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Mức phí phải là số hợp lệ.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thiếu thông tin");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        ((Stage) txtFeeName.getScene().getWindow()).close();
    }

    public void setEditingFee(Fee fee) {
        this.feeResult = fee;
        if (fee != null) {
            txtFeeName.setText(fee.getFeeName());
            cmbCategory.setValue(fee.getFeeCategory());
            cmbMethod.setValue(fee.getCalculationMethod());
            txtAmount.setText(String.valueOf(fee.getFeeAmount()));
        }
    }

    public Fee getResult() {
        return feeResult;
    }
}
