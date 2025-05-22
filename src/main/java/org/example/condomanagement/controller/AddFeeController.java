package org.example.condomanagement.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
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
            Fee fee = new Fee(
                    tenKhoanPhiField.getText(),
                    loaiComboBox.getValue(),
                    Double.parseDouble(soTienField.getText()),
                    cachTinhComboBox.getValue()
            );

            feeDao.saveOrUpdate(fee);

            // Thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thêm khoản phí");
            alert.setHeaderText(null);
            alert.setContentText("Đã thêm khoản phí thành công!");
            alert.showAndWait();

            // Đóng cửa sổ sau khi thêm
            ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();

            // Thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể thêm khoản phí");
            alert.setContentText("Vui lòng kiểm tra lại dữ liệu nhập vào.\nChi tiết lỗi: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        List<Fee.FeeCategory> feeTypeList=feeDao.findAllCategory();
        loaiComboBox.setItems(FXCollections.observableArrayList(Fee.FeeCategory.values()));
        List<Fee.CalculationMethod> feeMethodList=feeDao.findAllMethod();
        cachTinhComboBox.setItems(FXCollections.observableArrayList(Fee.CalculationMethod.values()));
    }

    public void setFeeToEdit(Fee selectedFee) {
        this.feeToEdit = selectedFee;

        // Gán dữ liệu vào các trường giao diện
        tenKhoanPhiField.setText(selectedFee.getFeeName());
        loaiComboBox.setValue(selectedFee.getFeeCategory()); // Enum
        soTienField.setText(String.valueOf(selectedFee.getFeeAmount()));
        cachTinhComboBox.setValue(selectedFee.getCalculationMethod()); // Enum
    }
}
