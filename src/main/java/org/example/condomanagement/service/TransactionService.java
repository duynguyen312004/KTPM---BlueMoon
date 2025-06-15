package org.example.condomanagement.service;

import org.example.condomanagement.dao.TransactionDao;
import org.example.condomanagement.model.TransactionView;

import java.time.LocalDate;
import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao = new TransactionDao();

    public List<String> getAllApartmentCodes() {
        return transactionDao.getDistinctApartmentCodes();
    }

    public List<String> getAllReceiptNumbers() {
        return transactionDao.getDistinctReceiptNumbers();
    }

    public List<TransactionView> filterTransactions(LocalDate from, LocalDate to,
            String apartmentCode, String receiptNumber) {
        // Truyền null nếu chuỗi rỗng
        String code = (apartmentCode != null && !apartmentCode.isBlank()) ? apartmentCode : null;
        String receipt = (receiptNumber != null && !receiptNumber.isBlank()) ? receiptNumber : null;
        return transactionDao.getTransactionViews(from, to, code, receipt);
    }
}
