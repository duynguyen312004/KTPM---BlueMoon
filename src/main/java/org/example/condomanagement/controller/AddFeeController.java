package org.example.condomanagement.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.dao.FeeDao;
import org.example.condomanagement.model.Fee;

import java.util.List;


public class AddFeeController {
    FeeDao feeDao = new FeeDao();

    private Fee feeToEdit;

    private TableView<Fee> feeTableView;

    @FXML
    private TextField tenKhoanPhiField;

    @FXML
    private ComboBox<Fee.FeeCategory> loaiComboBox;

    @FXML
    private TextField soTienField;

    @FXML
    private ComboBox<Fee.CalculationMethod> cachTinhComboBox;

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận hủy");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn hủy thao tác?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        try {
            String name = tenKhoanPhiField.getText();
            Fee.FeeCategory category = loaiComboBox.getValue();
            String amountText = soTienField.getText().trim();

            if (name.isEmpty() || amountText.isEmpty() || category == null || cachTinhComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount < 0) {
                showAlert(Alert.AlertType.ERROR, "Giá trị không hợp lệ", "Số tiền không được nhỏ hơn 0.");
                return;
            }

            Fee.CalculationMethod method = cachTinhComboBox.getValue();

            if (feeToEdit != null) {
                // ✅ Trường hợp SỬA
                feeToEdit.setFeeName(name);
                feeToEdit.setFeeCategory(category);
                feeToEdit.setFeeAmount(amount);
                feeToEdit.setCalculationMethod(method);

                feeDao.update(feeToEdit);
                feeTableView.refresh();
            } else {
                // ✅ Trường hợp THÊM MỚI
                Fee fee = new Fee(name, category, amount, method);
                Fee saved = feeDao.save(fee);
                feeTableView.getItems().add(saved);
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu khoản phí!");
            ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Vui lòng nhập số tiền hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu khoản phí\nChi tiết: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        List<Fee.FeeCategory> feeTypeList=feeDao.findAllCategory();
        loaiComboBox.setItems(FXCollections.observableArrayList(Fee.FeeCategory.values()));
        List<Fee.CalculationMethod> feeMethodList=feeDao.findAllMethod();
        cachTinhComboBox.setItems(FXCollections.observableArrayList(Fee.CalculationMethod.values()));
    }

    public void setFeeTableView(TableView<Fee> feeTableView) {
        this.feeTableView = feeTableView;
    }

    public void setFeeToEdit(Fee fee) {
        this.feeToEdit = fee;

        // Gán dữ liệu vào các trường giao diện
        tenKhoanPhiField.setText(fee.getFeeName());
        loaiComboBox.setValue(fee.getFeeCategory()); // Enum
        soTienField.setText(String.valueOf(fee.getFeeAmount()));
        cachTinhComboBox.setValue(fee.getCalculationMethod()); // Enum
    }

}
