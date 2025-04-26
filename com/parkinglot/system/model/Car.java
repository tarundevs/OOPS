package com.parkinglot.system.model;

import java.io.Serializable;

/**
 * Represents a car in the parking lot system.
 * Extends Vehicle to provide specific properties and behaviors for cars.
 * Implements Serializable to support persistence.
 */
public class Car extends Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isLuxury;

    /**
     * Constructs a car with the specified license plate.
     *
     * @param licensePlate The car's license plate
     */
    public Car(String licensePlate) {
        super(licensePlate, TYPE_CAR);
        this.isLuxury = false;
    }

    /**
     * Constructs a car with a luxury option.
     *
     * @param licensePlate The car's license plate
     * @param isLuxury     Whether the car is a luxury model
     */
    public Car(String licensePlate, boolean isLuxury) {
        super(licensePlate, TYPE_CAR);
        this.isLuxury = isLuxury;
    }


    /**
     * Returns whether the car is a luxury model.
     *
     * @return True if the car is luxury, false otherwise
     */
    public boolean isLuxury() {
        return isLuxury;
    }

    /**
     * Returns the base hourly parking rate for the car.
     *
     * @return The base parking rate (40.0 per hour, 48.0 for luxury)
     */
    @Override
    public double getBaseParkingRate() {
        return isLuxury ? 48.0 : 40.0;
    }
}