package org.example.condomanagement.model;

public class FeeCollectionRow {
    private Integer billingItemId;
    private String householdCode;
    private String feeName;
    private String batchName;
    private Double expectedAmount;
    private Double actualAmount;
    private String date;

    public FeeCollectionRow(Integer billingItemId, String householdCode, String feeName, String batchName, Double expectedAmount, Double actualAmount, String date) {
        this.billingItemId = billingItemId;
        this.householdCode = householdCode;
        this.feeName = feeName;
        this.batchName = batchName;
        this.expectedAmount = expectedAmount;
        this.actualAmount = actualAmount;
        this.date = date;
    }
    public Integer getBillingItemId() { return billingItemId; }
    public String getHouseholdCode() { return householdCode; }
    public String getFeeName() { return feeName; }
    public String getBatchName() { return batchName; }
    public Double getExpectedAmount() { return expectedAmount; }
    public Double getActualAmount() { return actualAmount; }
    public String getDate() { return date; }
} 