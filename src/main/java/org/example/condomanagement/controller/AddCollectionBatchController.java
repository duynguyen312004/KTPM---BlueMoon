package org.example.condomanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.condomanagement.dao.CollectionBatchDao;
import org.example.condomanagement.model.CollectionBatch;

import java.time.LocalDate;

public class AddCollectionBatchController {

    CollectionBatchDao collectionBatchDao = new CollectionBatchDao();

    @FXML private TextField batchNameField;
    @FXML private DatePicker batchDatePicker;

    @FXML
    private void onSave(ActionEvent event) {
        String name = batchNameField.getText().trim();
        LocalDate date = batchDatePicker.getValue();

        if (name.isEmpty() || date == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thiếu thông tin");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập đầy đủ tên đợt thu và ngày.");
            alert.showAndWait();
            return;
        }

        try {
            // Kiểm tra xem tên đợt thu đã tồn tại chưa
            boolean exists = collectionBatchDao.existsByName(name);
            if (exists) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Tên đợt thu đã tồn tại. Vui lòng nhập tên khác.");
                alert.showAndWait();
                return;
            }

            // Lưu mới vào DB
            CollectionBatch collectionBatch = new CollectionBatch(name, date);
            collectionBatchDao.save(collectionBatch);

            // Thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đã thêm đợt thu thành công.");
            alert.showAndWait();

            // Đóng cửa sổ
            ((Stage) batchNameField.getScene().getWindow()).close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi hệ thống");
            alert.setHeaderText("Đã xảy ra lỗi khi thêm đợt thu");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage) batchNameField.getScene().getWindow()).close();
    }
}
