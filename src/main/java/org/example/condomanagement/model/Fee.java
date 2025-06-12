package org.example.condomanagement.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/**
 * Entity class representing a fee in the condominium management system.
 * This class manages different types of fees that can be charged to households.
 */
@Entity
@Table(name = "fees")
public class Fee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Integer feeId;

    /**
     * The name of the fee (e.g., "Phi Quan Ly Chung Cu", "Phi Gui Xe")
     */
    @Column(name = "fee_name", nullable = false)
    private String feeName;

    /**
     * Categories of fees in the system
     * Service: Regular maintenance and service fees
     * Management: Administrative and management fees
     * Parking: Vehicle parking related fees
     * Utility: Basic utility fees
     * Voluntary: Optional contribution fees
     */
    public enum FeeCategory {
        Service, Management, Parking, Utility, Voluntary
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_category", nullable = false)
    private FeeCategory feeCategory;

    /**
     * The base amount for the fee
     */
    @Column(name = "fee_amount", nullable = false)
    private Double feeAmount;

    /**
     * Methods for calculating the fee amount
     * Fixed: Fixed amount regardless of other factors
     * PerSqM: Amount calculated based on square meters
     * PerVehicle: Amount calculated per vehicle
     */
    public enum CalculationMethod {
        Fixed, PerSqM, PerVehicle
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_method", nullable = false)
    private CalculationMethod calculationMethod;

    /**
     * List of billing items associated with this fee
     * One-to-many relationship with BillingItem
     */
    @OneToMany(mappedBy = "fee",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingItem> billingItems = new ArrayList<>();

    /**
     * List of batch fees associated with this fee
     * One-to-many relationship with BatchFee
     */
    @OneToMany(mappedBy = "fee",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchFee> batchFees = new ArrayList<>();

    //constructor
    public Fee() {

    }

    public Fee(String feeName, FeeCategory feeCategory, Double feeAmount, CalculationMethod calculationMethod) {
        this.feeName = feeName;
        this.feeCategory = feeCategory;
        this.feeAmount = feeAmount;
        this.calculationMethod = calculationMethod;
    }

    // getters & setters
    public Integer getFeeId() {
        return feeId;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public FeeCategory getFeeCategory() {
        return feeCategory;
    }

    public void setFeeCategory(FeeCategory feeCategory) {
        this.feeCategory = feeCategory;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public CalculationMethod getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(CalculationMethod calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
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

    @Override
    public String toString() {
        return this.feeName;
    }
}
