package org.example.condomanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "billing_item_id")
    private BillingItem billingItem;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Transaction() {
    }

    public Transaction(Integer transactionId, BillingItem billingItem, Double amountPaid, LocalDate paymentDate,
            User createdBy) {
        this.transactionId = transactionId;
        this.billingItem = billingItem;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.createdBy = createdBy;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public BillingItem getBillingItem() {
        return billingItem;
    }

    public void setBillingItem(BillingItem billingItem) {
        this.billingItem = billingItem;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

}