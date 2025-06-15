package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import org.example.condomanagement.model.Receipt;
import org.example.condomanagement.model.Transaction;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.model.User;
import javafx.scene.control.Alert;


public class ReceiptViewerController {

    @FXML private Label issueDateLabel;
    @FXML private Label receiptNumberLabel;
    @FXML private Label payerNameLabel;
    @FXML private Label apartmentCodeReceiptLabel;
    @FXML private Label paymentDetailsLabel;
    @FXML private Label amountInFiguresLabel;
    @FXML private Label amountInWordsLabel;
    @FXML private Label collectorNameReceiptLabel;
    @FXML private Button closeButton;
    @FXML private Label payerSignatureLabel;

    private Receipt receipt;
    private Runnable onReceiptClosed;

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
        updateUI();
    }

    public void setOnReceiptClosed(Runnable callback) {
        this.onReceiptClosed = callback;
    }

    private void updateUI() {
        if (receipt == null) return;
        Transaction transaction = receipt.getTransaction();
        BillingItem billingItem = transaction != null ? transaction.getBillingItem() : null;
        Household household = billingItem != null ? billingItem.getHousehold() : null;
        User collector = transaction != null ? transaction.getCreatedBy() : null;

        // Ngày phát hành - Lấy từ trường updated_at của BillingItem
        if (billingItem != null && billingItem.getUpdatedAt() != null) {
            issueDateLabel.setText("Ngày " + billingItem.getUpdatedAt().getDayOfMonth() +
                    " tháng " + billingItem.getUpdatedAt().getMonthValue() +
                    " năm " + billingItem.getUpdatedAt().getYear());
        }

        // Số biên lai
        receiptNumberLabel.setText(receipt.getReceiptNumber() != null ? "Số: " + receipt.getReceiptNumber() : "");

        // Tên người nộp - Lấy tên thành viên đầu tiên trong hộ gia đình
        if (household != null) {
            payerNameLabel.setText(household.getFirstResidentName());
        } else {
            payerNameLabel.setText("");
        }

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
            // Tên người nộp - Lấy tên thành viên đầu tiên trong hộ gia đình
        String payerName = "";
        if (household != null) {
            payerName = household.getFirstResidentName();
        }
        payerNameLabel.setText(payerName);
        payerSignatureLabel.setText(payerName); // Hiển thị tên ở phần chữ ký

    }

    // Hàm chuyển số thành chữ (bạn có thể dùng thư viện hoặc tự viết)
    private String convertNumberToWords(long number) {
        String[] units = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String[] teens = {"mười", "mười một", "mười hai", "mười ba", "mười bốn", "mười lăm", "mười sáu", "mười bảy", "mười tám", "mười chín"};
        String[] tens = {"", "mười", "hai mươi", "ba mươi", "bốn mươi", "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"};
        
        if (number == 0) return "không";
        
        String words = "";
        
        if ((number / 1000000000) > 0) {
            words += convertNumberToWords(number / 1000000000) + " tỷ ";
            number %= 1000000000;
        }
        
        if ((number / 1000000) > 0) {
            words += convertNumberToWords(number / 1000000) + " triệu ";
            number %= 1000000;
        }
        
        if ((number / 1000) > 0) {
            words += convertNumberToWords(number / 1000) + " nghìn ";
            number %= 1000;
        }
        
        if ((number / 100) > 0) {
            words += units[(int) (number / 100)] + " trăm ";
            number %= 100;
        }
        
        if (number > 0) {
            if (number < 10) {
                words += units[(int) number];
            } else if (number < 20) {
                words += teens[(int) (number - 10)];
            } else {
                words += tens[(int) (number / 10)];
                if ((number % 10) > 0) {
                    if ((number % 10) == 1) {
                        words += " mốt";
                    } else if ((number % 10) == 5) {
                        words += " lăm";
                    } else {
                        words += " " + units[(int) (number % 10)];
                    }
                }
            }
        }
        
        return words.trim();
    }

    @FXML
    private void handleClose() {
        if (onReceiptClosed != null) {
            onReceiptClosed.run();
        }
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Đã in Biên lai thành công!");
        alert.showAndWait();
    }

    @FXML
    private void handleSavePdf() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Đã lưu dưới định dạng PDF");
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        // Đóng biên lai khi nhấn nút Đóng
        // (giả sử bạn đã khai báo fx:id="closeButton" cho nút Đóng)
        // closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
    }
}
