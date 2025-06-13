// src/main/java/org/example/condomanagement/controller/PaymentManagementController.java
package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.condomanagement.model.FeeCollectionRow;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.service.FeeCollectionService;
import org.example.condomanagement.service.HouseholdService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentManagementController {
    @FXML
    private ComboBox<Household> householdComboBox;
    @FXML
    private Label apartmentCodeLabel;
    @FXML
    private Label ownerNameLabel;
    @FXML
    private TableView<FeeCollectionRow> billingItemsTable;
    @FXML
    private Button historyButton;
    @FXML
    private Button payButton;

    private final FeeCollectionService feeService = new FeeCollectionService();
    private final HouseholdService householdService = new HouseholdService();
    private ObservableList<FeeCollectionRow> masterData;
    private org.example.condomanagement.model.User loggedInUser;
    private Runnable onSuccessCallback;

    @FXML
    public void initialize() {
        // Load households into ComboBox
        List<Household> households = householdService.findAll();
        householdComboBox.setItems(FXCollections.observableArrayList(households));
        householdComboBox.setOnAction(e -> handleHouseholdSelection());

        // Load all fee collection data
        List<FeeCollectionRow> data = feeService.getAllFeeCollections();
        masterData = FXCollections.observableArrayList(data);
        billingItemsTable.setItems(masterData);

        // Enable payButton only if selection contains Pending
        payButton.setDisable(true);
        billingItemsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        billingItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            System.out.println("Selected: " + newSel);
            updatePayButtonState();
        });
        billingItemsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<FeeCollectionRow>) c -> updatePayButtonState());

        payButton.setOnAction(e -> openPaymentDialog());
    }

    private void updatePayButtonState() {
        boolean hasPending = billingItemsTable.getSelectionModel().getSelectedItems().stream()
                .anyMatch(row -> "Pending".equalsIgnoreCase(row.getStatus()));
        payButton.setDisable(!hasPending);
    }

    private void openPaymentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentDialog.fxml"));
            Parent root = loader.load();
            PaymentDialogController controller = loader.getController();

            // Lấy các dòng được chọn
            List<FeeCollectionRow> selectedRows = billingItemsTable.getSelectionModel().getSelectedItems();
            System.out.println("Selected rows: " + selectedRows.size());
            if (selectedRows == null || selectedRows.isEmpty()) {
                showAlert("Bạn phải chọn ít nhất một khoản phí để thanh toán!");
                return;
            }

            controller.setData(selectedRows, loggedInUser, onSuccessCallback);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Xác nhận thanh toán");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleHouseholdSelection() {
        Household selected = householdComboBox.getValue();
        if (selected == null) {
            apartmentCodeLabel.setText("[...]");
            ownerNameLabel.setText("[...]");
            billingItemsTable.setItems(masterData);
            return;
        }
        apartmentCodeLabel.setText(selected.getApartmentCode());
        String ownerName = selected.getResidents().stream()
                .filter(r -> r.getResidentId().equals(selected.getHeadResidentId()))
                .map(r -> r.getName())
                .findFirst().orElse("[...]");
        ownerNameLabel.setText(ownerName);

        ObservableList<FeeCollectionRow> filtered = masterData.stream()
                .filter(row -> selected.getApartmentCode().equals(row.getHouseholdCode()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        billingItemsTable.setItems(filtered);
    }

    public void setLoggedInUser(org.example.condomanagement.model.User user) {
        this.loggedInUser = user;
    }

    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }
}