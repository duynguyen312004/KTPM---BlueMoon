package org.example.condomanagement.service;

import org.example.condomanagement.model.FeeCollectionRow;
import org.example.condomanagement.model.User;
import org.example.condomanagement.dao.TransactionDao;
import org.example.condomanagement.dao.BillingItemDao;
import org.example.condomanagement.dao.ReceiptDao;
import org.example.condomanagement.model.Transaction;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.Receipt;
import org.example.condomanagement.dao.UserDao;

import java.time.LocalDate;
import java.util.List;

public class PaymentService {
    private final TransactionDao transactionDao = new TransactionDao();
    private final BillingItemDao billingItemDao = new BillingItemDao();
    private final ReceiptDao receiptDao = new ReceiptDao();
    private final UserDao userDao = new UserDao();

    public void processPayment(List<FeeCollectionRow> rows, double amount, LocalDate date, String note, User user) {
        // 1. Insert into transactions (one per row or one for all, as per your schema)
        // 2. Update billing_items: add to actual_amount, set status if paid
        // 3. Insert into receipts, link to transaction
        // 4. Commit transaction
        // (Implement DB logic here)
    }

    public int createTransaction(int billingId, double amount, LocalDate date, String note, int userId) {
        BillingItem billingItem = billingItemDao.findById(billingId);
        User user = userDao.findById(userId);
        Transaction transaction = new Transaction();
        transaction.setBillingItem(billingItem);
        transaction.setAmountPaid(amount);
        transaction.setPaymentDate(date);
        transaction.setCreatedBy(user);
        transactionDao.save(transaction);
        return transaction.getTransactionId();
    }

    public void updateBillingItemAfterPayment(int billingId, double amount) {
        BillingItem item = billingItemDao.findById(billingId);
        double newActual = item.getActualAmount() + amount;
        item.setActualAmount(newActual);
        if (newActual >= item.getExpectedAmount()) {
            item.setStatus(BillingItem.Status.Paid);
        }
        billingItemDao.save(item);
    }

    public void createReceipt(int transactionId) {
        Receipt receipt = new Receipt();
        Transaction transaction = transactionDao.findById(transactionId);
        receipt.setTransaction(transaction);
        receipt.setIssueDate(LocalDate.now());
        receiptDao.save(receipt);
    }
}