package org.example.condomanagement.controller;

import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import org.example.condomanagement.dao.FeeDao;
import org.example.condomanagement.model.Fee;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeeManagementController {

    FeeDao feeDao = new FeeDao();

    @FXML
    private TextField searchFeeTextField;

    @FXML
    private ComboBox<String> feeTypeComboBox;

    @FXML
    TableView<Fee> feeTableView;
    @FXML
    private TableColumn<Fee, String> maKhoanPhiColumn;
    @FXML
    private TableColumn<Fee, String> tenKhoanPhiColumn;
    @FXML
    private TableColumn<Fee, String> loaiColumn;
    @FXML
    private TableColumn<Fee, Double> soTienColumn;
    @FXML
    private TableColumn<Fee, String> cachtinhColumn;

    @FXML
    public void searchFeeButton(javafx.event.ActionEvent actionEvent) {
        String selectedName = feeTypeComboBox.getValue();
        String keyword = searchFeeTextField.getText().toLowerCase().trim();

        List<Fee> allFees = feeDao.findAll();
        List<Fee> filtered = allFees.stream()
                .filter(fee -> {
                    boolean matchName = selectedName.equals("Tất cả") || fee.getFeeCategory().toString().equals(selectedName);
                    boolean matchKeyword = keyword.isEmpty() || fee.getFeeName().toLowerCase().contains(keyword);
                    return matchName && matchKeyword;
                })
                .toList();

        feeTableView.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void addFeeButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_fee.fxml"));
            Parent root = loader.load();

            // Truyền observable list hiện tại cho controller
            AddFeeController controller = loader.getController();
            controller.setFeeTableView(feeTableView); // Truyền danh sách đang hiển thị

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
        Fee selectedFee = feeTableView.getSelectionModel().getSelectedItem();

        if (selectedFee == null) {
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
            AddFeeController controller = loader.getController();

             // ✅ Truyền fee được chọn
            controller.setFeeToEdit(selectedFee);

             // ✅ Truyền cả feeTableView (QUAN TRỌNG!)
            controller.setFeeTableView(feeTableView); // <-- bạn đang thiếu dòng này

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
        Fee selectedFee = feeTableView.getSelectionModel().getSelectedItem();

        if (selectedFee == null) {
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
        confirm.setContentText("Bạn có chắc chắn muốn xóa khoản phí \"" + selectedFee.getFeeName() + "\"?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                feeDao.delete(selectedFee);  // Gọi DAO để xóa trong DB
                feeTableView.getItems().remove(selectedFee);  // Xóa khỏi giao diện

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
        feeTypeComboBox.setValue("Tất cả");
        searchFeeTextField.clear();
        feeTableView.setItems(FXCollections.observableArrayList(feeDao.findAll()));
    }

    @FXML
    public void initialize() {
        List<String> categoryList = Arrays.stream(Fee.FeeCategory.values())
                .map(Enum::name) // "Management", "Parking", ...
                .toList();

        List<String> comboItems = new ArrayList<>();
        comboItems.add("Tất cả");
        comboItems.addAll(categoryList);

        feeTypeComboBox.setItems(FXCollections.observableArrayList(comboItems));
        feeTypeComboBox.setValue("Tất cả");


        ObservableList<Fee> feeList= FXCollections.observableArrayList(feeDao.findAll());
        feeTableView.setItems(feeList);
        maKhoanPhiColumn.setCellValueFactory(cellData-> new SimpleIntegerProperty(cellData.getValue().getFeeId()).asObject().asString());
        tenKhoanPhiColumn.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getFeeName()));
        loaiColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getFeeCategory().toString()));
        soTienColumn.setCellValueFactory(cellData->new SimpleDoubleProperty(cellData.getValue().getFeeAmount()).asObject());
        cachtinhColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getCalculationMethod().toString()));
    }
}
