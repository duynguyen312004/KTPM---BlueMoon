package org.example.condomanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_fee_mapping")
public class VehicleFeeMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, unique = true)
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
