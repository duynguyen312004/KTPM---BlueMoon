package org.example.condomanagement.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.example.condomanagement.model.TransactionView;
import org.example.condomanagement.service.TransactionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryController {
        @FXML
        private DatePicker dateFromPicker;
        @FXML
        private DatePicker dateToPicker;
        @FXML
        private ComboBox<String> apartmentCodeComboBox;
        @FXML
        private ComboBox<String> receiptNumberComboBox;
        @FXML
        private Button filterButton;
        @FXML
        private Button clearFilterButton;
        @FXML
        private TableView<TransactionView> transactionsTable;
        @FXML
        private TableColumn<TransactionView, String> receiptColumn;
        @FXML
        private TableColumn<TransactionView, LocalDate> paymentDateColumn;
        @FXML
        private TableColumn<TransactionView, String> apartmentCodeColumn;
        @FXML
        private TableColumn<TransactionView, String> feeDetailsColumn;
        @FXML
        private TableColumn<TransactionView, Double> amountPaidColumn;
        @FXML
        private TableColumn<TransactionView, String> collectorColumn;

        private final TransactionService service = new TransactionService();

        @FXML
        public void initialize() {
                // Custom định dạng DatePicker: dd/MM/yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                StringConverter<LocalDate> converter = new StringConverter<>() {
                        @Override
                        public String toString(LocalDate date) {
                                return date != null ? formatter.format(date) : "";
                        }

                        @Override
                        public LocalDate fromString(String string) {
                                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter)
                                                : null;
                        }
                };
                dateFromPicker.setConverter(converter);
                dateToPicker.setConverter(converter);

                // Gán dữ liệu bảng
                receiptColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNumber()));
                paymentDateColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPaymentDate()));
                apartmentCodeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApartmentCode()));
                feeDetailsColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeeDetails()));
                amountPaidColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAmountPaid()));
                collectorColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCollectorName()));

                apartmentCodeComboBox.setItems(FXCollections.observableArrayList(service.getAllApartmentCodes()));
                receiptNumberComboBox.setItems(FXCollections.observableArrayList(service.getAllReceiptNumbers()));

                filterButton.setOnAction(e -> loadFilteredData());
                clearFilterButton.setOnAction(e -> clearFilters());

                loadFilteredData();
        }

        private void loadFilteredData() {
                LocalDate from = dateFromPicker.getValue();
                LocalDate to = dateToPicker.getValue();

                // ⚠️ Check logic ngày lọc
                if (from != null && to != null && from.isAfter(to)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Lỗi lọc ngày");
                        alert.setHeaderText(null);
                        alert.setContentText("Ngày bắt đầu không được sau ngày kết thúc.");
                        alert.showAndWait();
                        return;
                }

                String apartmentCode = apartmentCodeComboBox.getValue();
                String receiptNumber = receiptNumberComboBox.getValue();

                List<TransactionView> results = service.filterTransactions(from, to, apartmentCode, receiptNumber);
                transactionsTable.setItems(FXCollections.observableArrayList(results));
        }

        private void clearFilters() {
                dateFromPicker.setValue(null);
                dateToPicker.setValue(null);
                apartmentCodeComboBox.getSelectionModel().clearSelection();
                receiptNumberComboBox.getSelectionModel().clearSelection();
                loadFilteredData();
        }
}
