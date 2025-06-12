package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.model.FeeCollectionRow;
import org.example.condomanagement.service.PaymentService;
import org.example.condomanagement.model.User;
import org.example.condomanagement.model.BillingItem;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PaymentDialogController {
    @FXML private TextField amountPaidField;
    @FXML private DatePicker paymentDatePicker;
    @FXML private TextArea noteArea;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private Label feeDetailsLabel;

    private List<FeeCollectionRow> selectedRows;
    private User currentUser;
    private Runnable onPaymentSuccess; // Callback to open receipt viewer
    private BillingItem billingItem;

    // In PaymentDialogController.java, update setData:
    public void setData(List<FeeCollectionRow> rows, User user, Runnable onSuccess) {
        this.selectedRows = rows;
        this.currentUser = user;
        this.onPaymentSuccess = onSuccess;

        // Hiển thị tên khoản phí
        if (rows.size() == 1) {
            feeDetailsLabel.setText(rows.get(0).getFeeName());
            amountPaidField.setText(String.valueOf(rows.get(0).getRemainingAmount()));
        } else {
            double total = rows.stream().mapToDouble(FeeCollectionRow::getRemainingAmount).sum();
            feeDetailsLabel.setText("Nhiều khoản phí (" + rows.size() + " khoản)");
            amountPaidField.setText(String.valueOf(total));
        }

        paymentDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleConfirm() {
        try {
            double amount = Double.parseDouble(amountPaidField.getText().replace(",", ""));
            if (amount <= 0) {
                showAlert("Số tiền phải lớn hơn 0.");
                return;
            }
            LocalDate date = paymentDatePicker.getValue();
            if (date == null) {
                showAlert("Vui lòng chọn ngày thanh toán.");
                return;
            }

            PaymentService paymentService = new PaymentService();
            for (FeeCollectionRow row : selectedRows) {
                // 1. Tạo transaction
                int transactionId = paymentService.createTransaction(
                    row.getBillingItemId(), amount, date, noteArea.getText(), currentUser.getUserId()
                );
                // 2. Cập nhật billing_item
                paymentService.updateBillingItemAfterPayment(row.getBillingItemId(), amount);

                // 3. Tạo receipt
                paymentService.createReceipt(transactionId);
            }

            // 4. Đóng dialog
            ((Stage) confirmButton.getScene().getWindow()).close();

            // 5. Mở màn hình ReceiptViewer
            openReceiptViewer();

            // 6. Callback nếu cần
            if (onPaymentSuccess != null) onPaymentSuccess.run();

        } catch (NumberFormatException e) {
            showAlert("Số tiền không hợp lệ.");
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public void setBillingItem(BillingItem billingItem) {
        this.billingItem = billingItem;
        updateUI();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    private void updateUI() {
        if (billingItem != null) {
            // Display fee name
            if (billingItem.getFee() != null) {
                feeDetailsLabel.setText(billingItem.getFee().getFeeName());
            }
        }
    }

    // Hàm mở màn hình ReceiptViewer
    private void openReceiptViewer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReceiptViewer.fxml"));
            Parent root = loader.load();
            // Nếu cần truyền receipt, bạn có thể lấy controller và gọi setReceipt()
            // ReceiptViewerController controller = loader.getController();
            // controller.setReceipt(receipt);
            Stage stage = new Stage();
            stage.setTitle("Biên lai thanh toán");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình biên lai.");
        }
    }
}