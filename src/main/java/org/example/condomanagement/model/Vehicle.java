package org.example.condomanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "household_id")
    private Household household;

    public enum VehicleType {
        MOTORBIKE, CAR
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VehicleType type;

    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    public Vehicle() {
    }

    public Vehicle(Integer vehicleId, Household household, VehicleType type, String plateNumber) {
        this.vehicleId = vehicleId;
        this.household = household;
        this.type = type;
        this.plateNumber = plateNumber;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

}
