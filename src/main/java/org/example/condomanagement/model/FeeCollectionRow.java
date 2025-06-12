package org.example.condomanagement.model;

public class FeeCollectionRow {
    private Integer billingItemId;
    private String householdCode;
    private String feeName;
    private String batchName;
    private Double expectedAmount;
    private Double actualAmount;
    private String date;
    private String status; // <-- Add this line
    private String batchPeriod;

    public FeeCollectionRow(Integer billingItemId, String householdCode, String feeName, String batchName, Double expectedAmount, Double actualAmount, String date, String status) {
        this.billingItemId = billingItemId;
        this.householdCode = householdCode;
        this.feeName = feeName;
        this.batchName = batchName;
        this.expectedAmount = expectedAmount;
        this.actualAmount = actualAmount;
        this.date = date;
        this.status = status; // <-- Initialize status
        this.batchPeriod = batchName;
    }
    public Integer getBillingItemId() { return billingItemId; }
    public String getHouseholdCode() { return householdCode; }
    public String getFeeName() { return feeName; }
    public String getBatchName() { return batchName; }
    public Double getExpectedAmount() { return expectedAmount; }
    public Double getActualAmount() { return actualAmount; }
    public String getDate() { return date; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getBatchPeriod() {
        return batchPeriod;
    }

    public void setBatchPeriod(String batchPeriod) {
        this.batchPeriod = batchPeriod;
    }

    public Double getRemainingAmount() {
        if (expectedAmount == null) return 0.0;
        if (actualAmount == null) return expectedAmount;
        return expectedAmount - actualAmount;
    }
}