package org.example.condomanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "batch_fees")
public class BatchFee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_fee_id")
    private Integer batchFeeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batch_id")
    private CollectionBatch batch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fee_id")
    private Fee fee;

    // getters & setters
    public Integer getBatchFeeId() {
        return batchFeeId;
    }

    public CollectionBatch getBatch() {
        return batch;
    }

    public void setBatch(CollectionBatch batch) {
        this.batch = batch;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }
}
