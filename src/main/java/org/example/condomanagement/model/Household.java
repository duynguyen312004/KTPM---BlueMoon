package org.example.condomanagement.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "households")
public class Household extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "household_id")
    private Integer householdId;

    @Column(name = "apartment_code", nullable = false, unique = true)
    private String apartmentCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double area; // diện tích m²

    @OneToMany(mappedBy = "household",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residents = new ArrayList<>();

    @OneToMany(mappedBy = "household")
    private List<BillingItem> billingItems = new ArrayList<>();

    @OneToMany(mappedBy = "household")
    private List<Vehicle> vehicles = new ArrayList<>();

    @Column(name = "head_resident_id")
    private Integer headResidentId;




    // getters & setters
    public Integer getHouseholdId() {
        return householdId;
    }

    public String getApartmentCode() {
        return apartmentCode;
    }

    public void setApartmentCode(String apartmentCode) {
        this.apartmentCode = apartmentCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setHouseholdId(Integer householdId) {
        this.householdId = householdId;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    public List<BillingItem> getBillingItems() {
        return billingItems;
    }

    public void setBillingItems(List<BillingItem> billingItems) {
        this.billingItems = billingItems;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Integer getHeadResidentId() {
        return headResidentId;
    }
    public void setHeadResidentId(Integer headResidentId) {
        this.headResidentId = headResidentId;
    }
    @Override
    public String toString() {
        return apartmentCode;

    }
    }
