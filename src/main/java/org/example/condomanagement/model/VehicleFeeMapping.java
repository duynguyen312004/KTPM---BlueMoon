package org.example.condomanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_fee_mapping")
public class VehicleFeeMapping {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private Vehicle.VehicleType vehicleType;

    @OneToOne(optional = false)
    @JoinColumn(name = "fee_id")
    private Fee fee;

    // getters & setters
    public Vehicle.VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Vehicle.VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }
}
