package org.example.condomanagement.model;

import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "billing_items")
public class BillingItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_item_id")
    private Integer billingItemId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fee_id")
    private Fee fee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batch_id")
    private CollectionBatch batch;

    @Column(name = "expected_amount", nullable = false)
    private Double expectedAmount;

    @Column(name = "actual_amount", nullable = false)
    private Double actualAmount;

    public enum Status {
        Pending, Paid
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "billingItem",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public BillingItem() {
    }

    public BillingItem(Integer billingItemId, Household household, Fee fee, CollectionBatch batch,
            Double expectedAmount, Double actualAmount, Status status, List<Transaction> transactions) {
        this.billingItemId = billingItemId;
        this.household = household;
        this.fee = fee;
        this.batch = batch;
        this.expectedAmount = expectedAmount;
        this.actualAmount = actualAmount;
        this.status = status;
        this.transactions = transactions;
    }

    public Integer getBillingItemId() {
        return billingItemId;
    }

    public void setBillingItemId(Integer billingItemId) {
        this.billingItemId = billingItemId;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public CollectionBatch getBatch() {
        return batch;
    }

    public void setBatch(CollectionBatch batch) {
        this.batch = batch;
    }

    public Double getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(Double expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}