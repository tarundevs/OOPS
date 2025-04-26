package com.parkinglot.system.model;

import java.io.Serializable;

/**
 * Represents an electric vehicle in the parking lot system.
 * Extends Vehicle to provide specific properties and behaviors for electric vehicles.
 * Implements Serializable to support persistence.
 */
public class ElectricVehicle extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private double batteryCapacity;

    /**
     * Constructs an electric vehicle with the specified license plate and battery capacity.
     *
     * @param licensePlate    The electric vehicle's license plate
     * @param batteryCapacity The battery capacity in kWh
     */
    public ElectricVehicle(String licensePlate, double batteryCapacity) {
        super(licensePlate, TYPE_CAR);
        this.batteryCapacity = batteryCapacity;
    }

    /**
     * Constructs an electric vehicle with a handicapped permit option.
     *
     * @param licensePlate     The electric vehicle's license plate
     * @param handicappedPermit Whether the electric vehicle has a handicapped permit
     */
    public ElectricVehicle(String licensePlate, boolean handicappedPermit) {
        super(licensePlate, TYPE_CAR, handicappedPermit);
        this.batteryCapacity = 50.0; // Default capacity
    }

    /**
     * Returns the battery capacity.
     *
     * @return The battery capacity in kWh
     */
    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    /**
     * Returns the base hourly parking rate for the electric vehicle.
     *
     * @return The base parking rate (48.0 per hour)
     */
    @Override
    public double getBaseParkingRate() {
        return 48.0;
    }
}