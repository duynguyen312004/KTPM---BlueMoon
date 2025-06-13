package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.condomanagement.model.Receipt;
import org.example.condomanagement.model.Transaction;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.model.User;

public class ReceiptViewerController {

    @FXML private Label issueDateLabel;
    @FXML private Label receiptNumberLabel;
    @FXML private Label payerNameLabel;
    @FXML private Label apartmentCodeReceiptLabel;
    @FXML private Label paymentDetailsLabel;
    @FXML private Label amountInFiguresLabel;
    @FXML private Label amountInWordsLabel;
    @FXML private Label collectorNameReceiptLabel;

    private Receipt receipt;

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
        updateUI();
    }

    private void updateUI() {
        if (receipt == null) return;
        Transaction transaction = receipt.getTransaction();
        BillingItem billingItem = transaction != null ? transaction.getBillingItem() : null;
        Household household = billingItem != null ? billingItem.getHousehold() : null;
        User collector = transaction != null ? transaction.getCreatedBy() : null;

        // Ngày phát hành
        if (receipt.getIssueDate() != null) {
            issueDateLabel.setText("Ngày " + receipt.getIssueDate().getDayOfMonth() +
                    " tháng " + receipt.getIssueDate().getMonthValue() +
                    " năm " + receipt.getIssueDate().getYear());
        }

        // Số biên lai
        receiptNumberLabel.setText(receipt.getReceiptNumber() != null ? "Số: " + receipt.getReceiptNumber() : "");

        // Tên người nộp
        payerNameLabel.setText(household != null ? household.toString() : "");

        // Mã căn hộ
        apartmentCodeReceiptLabel.setText(household != null ? household.getApartmentCode() : "");

        // Nội dung thanh toán
        if (billingItem != null && billingItem.getFee() != null) {
            paymentDetailsLabel.setText("Thanh toán " + billingItem.getFee().getFeeName());
        }

        // Số tiền
        if (transaction != null && transaction.getAmountPaid() != null) {
            amountInFiguresLabel.setText(String.format("%,.0f VNĐ", transaction.getAmountPaid()));
            amountInWordsLabel.setText("[" + convertNumberToWords(transaction.getAmountPaid().longValue()) + " đồng chẵn]");
        }

        // Người thu tiền
        if (collector != null) {
            collectorNameReceiptLabel.setText(collector.getFullName());
            collectorNameReceiptLabel.setVisible(true);
        }
    }

    // Hàm chuyển số thành chữ (bạn có thể dùng thư viện hoặc tự viết)
    private String convertNumberToWords(long number) {
        // TODO: Viết hàm chuyển số thành chữ tiếng Việt
        return "Một triệu năm trăm nghìn"; // ví dụ
    }

    @FXML
    private void initialize() {
        // Đóng biên lai khi nhấn nút Đóng
        // (giả sử bạn đã khai báo fx:id="closeButton" cho nút Đóng)
        // closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
    }
}
