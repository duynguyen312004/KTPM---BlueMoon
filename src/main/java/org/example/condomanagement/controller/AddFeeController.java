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

@SuppressWarnings("unused")
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
            double amount = Double.parseDouble(soTienField.getText());
            Fee.CalculationMethod method = cachTinhComboBox.getValue();

            if (feeToEdit != null) {
                // ✅ Trường hợp SỬA
                feeToEdit.setFeeName(name);
                feeToEdit.setFeeCategory(category);
                feeToEdit.setFeeAmount(amount);
                feeToEdit.setCalculationMethod(method);

                feeDao.update(feeToEdit); // <-- dùng update thay vì merge
                feeTableView.refresh(); // Làm mới giao diện

            } else {
                // ✅ Trường hợp THÊM MỚI
                Fee fee = new Fee(name, category, amount, method);
                Fee saved = feeDao.save(fee);
                feeTableView.getItems().add(saved);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đã lưu khoản phí!");
            alert.showAndWait();

            ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu khoản phí");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        List<Fee.FeeCategory> feeTypeList = feeDao.findAllCategory();
        loaiComboBox.setItems(FXCollections.observableArrayList(Fee.FeeCategory.values()));
        List<Fee.CalculationMethod> feeMethodList = feeDao.findAllMethod();
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
