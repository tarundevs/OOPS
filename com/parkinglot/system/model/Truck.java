package com.parkinglot.system.model;

import java.io.Serializable;

/**
 * Represents a truck in the parking lot system.
 * Extends Vehicle to provide specific properties and behaviors for trucks.
 * Implements Serializable to support persistence.
 */
public class Truck extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private double weight;

    /**
     * Constructs a truck with the specified license plate and weight.
     *
     * @param licensePlate The truck's license plate
     * @param weight       The truck's weight in tons
     */
    public Truck(String licensePlate, double weight) {
        super(licensePlate, TYPE_TRUCK);
        this.weight = weight;
    }

    /**
     * Constructs a truck with a handicapped permit option.
     *
     * @param licensePlate     The truck's license plate
     * @param handicappedPermit Whether the truck has a handicapped permit
     */
    public Truck(String licensePlate, boolean handicappedPermit) {
        super(licensePlate, TYPE_TRUCK, handicappedPermit);
        this.weight = 1.0; // Default weight
    }

    /**
     * Returns the truck's weight.
     *
     * @return The weight in tons
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the base hourly parking rate for the truck.
     *
     * @return The base parking rate (80.0 per hour)
     */
    @Override
    public double getBaseParkingRate() {
        return 80.0;
    }
}