package org.example.condomanagement.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.condomanagement.dao.BillingItemDao;
import org.example.condomanagement.dao.HouseholdDao;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.Household;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportingController {

    @FXML
    private ComboBox<String> statTypeComboBox;
    @FXML
    private ComboBox<String> objectComboBox;
    @FXML
    private DatePicker fromDateDatePicker;
    @FXML
    private DatePicker toDateDatePicker;
    @FXML
    private Button filterButton;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label collectedAmountLabel;
    @FXML
    private Label pendingAmountLabel;
    @FXML
    private Label collectionRateLabel;
    @FXML
    private Label topDebtor1AmountLabel;
    @FXML
    private Label topDebtor2AmountLabel;
    @FXML
    private Label topDebtor3AmountLabel;
    @FXML
    private TableView<BillingItemDao.BillingSummary> detailsTableView;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> householdIdColumn;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> householdNameColumn;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> amountCollectedColumn;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> amountDueColumn;

    private final HouseholdDao householdDao = new HouseholdDao();
    private final BillingItemDao billingItemDao = new BillingItemDao();

    @FXML
    void initialize() {
        List<BillingItemDao.BillingSummary> billingItemList=billingItemDao.getBillingSummaryByHousehold();
        detailsTableView.setItems(FXCollections.observableArrayList(billingItemList));
        householdIdColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getHouseholdId()));
        householdNameColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getHouseholdName()));
        amountCollectedColumn.setCellValueFactory(cellData->new SimpleStringProperty(formatCurrency(cellData.getValue().getTotalActualAmount())));
        amountDueColumn.setCellValueFactory(cellData->new SimpleStringProperty(formatCurrency(cellData.getValue().getTotalExpectedAmount()-cellData.getValue().getTotalActualAmount())));

    }

    private String formatCurrency(Double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }

}
