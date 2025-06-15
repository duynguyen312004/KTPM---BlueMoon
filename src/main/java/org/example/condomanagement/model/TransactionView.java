package org.example.condomanagement.model;

import java.time.LocalDate;

public class TransactionView {
    private String receiptNumber;
    private LocalDate paymentDate;
    private String apartmentCode;
    private String feeDetails;
    private Double amountPaid;
    private String collectorName;

    public TransactionView(String receiptNumber, LocalDate paymentDate, String apartmentCode,
            String feeDetails, Double amountPaid, String collectorName) {
        this.receiptNumber = receiptNumber;
        this.paymentDate = paymentDate;
        this.apartmentCode = apartmentCode;
        this.feeDetails = feeDetails;
        this.amountPaid = amountPaid;
        this.collectorName = collectorName;
    }

    // Getters
    public String getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getApartmentCode() {
        return apartmentCode;
    }

    public String getFeeDetails() {
        return feeDetails;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public String getCollectorName() {
        return collectorName;
    }
}
