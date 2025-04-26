package com.parkinglot.system.model;

import java.io.Serializable;

/**
 * Represents a bike in the parking lot system.
 * Extends Vehicle to provide specific properties and behaviors for bikes.
 * Implements Serializable to support persistence.
 */
public class Bike extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a bike with the specified license plate.
     *
     * @param licensePlate The bike's license plate
     */
    public Bike(String licensePlate) {
        super(licensePlate, TYPE_BIKE);
    }

    /**
     * Constructs a bike with a handicapped permit option.
     *
     * @param licensePlate     The bike's license plate
     * @param handicappedPermit Whether the bike has a handicapped permit
     */
    public Bike(String licensePlate, boolean handicappedPermit) {
        super(licensePlate, TYPE_BIKE, handicappedPermit);
    }

    /**
     * Returns the base hourly parking rate for the bike.
     *
     * @return The base parking rate (20.0 per hour)
     */
    @Override
    public double getBaseParkingRate() {
        return 20.0;
    }
}