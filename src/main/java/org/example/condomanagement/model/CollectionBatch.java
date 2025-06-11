package org.example.condomanagement.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collection_batches")
public class CollectionBatch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Integer batchId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate period;

    @OneToMany(mappedBy = "batch",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingItem> billingItems = new ArrayList<>();

    @OneToMany(mappedBy = "batch",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchFee> batchFees = new ArrayList<>();

    public CollectionBatch(String name, LocalDate period) {
        this.name = name;
        this.period = period;
    }

    public CollectionBatch() {}

    // getters & setters
    public Integer getBatchId() {
        return batchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public List<BillingItem> getBillingItems() {
        return billingItems;
    }

    public void setBillingItems(List<BillingItem> billingItems) {
        this.billingItems = billingItems;
    }

    public List<BatchFee> getBatchFees() {
        return batchFees;
    }

    public void setBatchFees(List<BatchFee> batchFees) {
        this.batchFees = batchFees;
    }

}
