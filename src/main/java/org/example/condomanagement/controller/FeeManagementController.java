package org.example.condomanagement.controller;

import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.condomanagement.dao.FeeDao;
import org.example.condomanagement.model.Fee;

import java.time.LocalDate;
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
    }

    @FXML
    public void addFeeButton(ActionEvent actionEvent) {
    }

    @FXML
    public void updateFeeButton(ActionEvent actionEvent) {
    }

    @FXML
    public void deleteFeeButton(ActionEvent actionEvent) {
    }

    @FXML
    public void backButton(ActionEvent actionEvent) {
    }

    @FXML
    public void initialize() {
        List<String> fees=feeDao.findAllName();
        feeTypeComboBox.setItems(FXCollections.observableArrayList(fees));
        ObservableList<Fee> feeList= FXCollections.observableArrayList(feeDao.findAll());
        feeTableView.setItems(feeList);
        maKhoanPhiColumn.setCellValueFactory(cellData-> new SimpleIntegerProperty(cellData.getValue().getFeeId()).asObject().asString());
        tenKhoanPhiColumn.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getFeeName()));
        loaiColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getFeeCategory().toString()));
        soTienColumn.setCellValueFactory(cellData->new SimpleDoubleProperty(cellData.getValue().getFeeAmount()).asObject());
        cachtinhColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getCalculationMethod().toString()));
    }



}
