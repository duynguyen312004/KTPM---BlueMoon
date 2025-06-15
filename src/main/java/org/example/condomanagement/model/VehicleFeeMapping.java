package org.example.condomanagement.model;

import jakarta.persistence.*;
import org.example.condomanagement.model.VehicleType;

@SuppressWarnings("unused")
@Entity
@Table(name = "vehicle_fee_mapping")
public class VehicleFeeMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @ManyToOne
    @JoinColumn(name = "fee_id")
    private Fee fee;

    // getters & setters
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }
}
