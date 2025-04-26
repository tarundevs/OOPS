package com.parkinglot.system.model;

import java.io.Serializable;

/**
 * Abstract base class for vehicles in the parking lot system.
 * Defines common properties and behaviors for all vehicle types.
 * Implements Serializable to support persistence.
 */
public abstract class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_CAR = "CAR";
    public static final String TYPE_BIKE = "BIKE";
    public static final String TYPE_TRUCK = "TRUCK";
    public static final String TYPE_BUS = "BUS";

    private String licensePlate;
    private String type;
    private boolean handicappedPermit;

    /**
     * Constructs a Vehicle with a specified license plate and type.
     *
     * @param licensePlate The license plate of the vehicle
     * @param type         The type of vehicle (e.g., CAR, BIKE, TRUCK, BUS)
     * @throws IllegalArgumentException If the vehicle type is invalid
     */
    public Vehicle(String licensePlate, String type) {
        validateVehicleType(type);
        this.licensePlate = licensePlate.toUpperCase();
        this.type = type;
        this.handicappedPermit = false;
    }

    /**
     * Constructs a Vehicle with a specified license plate, type, and handicapped permit status.
     *
     * @param licensePlate      The license plate of the vehicle
     * @param type              The type of vehicle (e.g., CAR, BIKE, TRUCK, BUS)
     * @param handicappedPermit Whether the vehicle has a handicapped permit
     * @throws IllegalArgumentException If the vehicle type is invalid
     */
    public Vehicle(String licensePlate, String type, boolean handicappedPermit) {
        validateVehicleType(type);
        this.licensePlate = licensePlate.toUpperCase();
        this.type = type;
        this.handicappedPermit = handicappedPermit;
    }

    /**
     * Validates the vehicle type to ensure it is one of the allowed types.
     *
     * @param type The vehicle type to validate
     * @throws IllegalArgumentException If the vehicle type is invalid
     */
    public static void validateVehicleType(String type) {
        if (type == null || !(type.equals(TYPE_CAR) || type.equals(TYPE_BIKE) ||
                              type.equals(TYPE_TRUCK) || type.equals(TYPE_BUS))) {
            throw new IllegalArgumentException("Invalid vehicle type: " + type);
        }
    }

    /**
     * Retrieves the base parking rate for the vehicle.
     *
     * @return The base parking rate
     */
    public abstract double getBaseParkingRate();

    /**
     * Retrieves the license plate of the vehicle.
     *
     * @return The license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Retrieves the type of the vehicle.
     *
     * @return The vehicle type (e.g., CAR, BIKE, TRUCK, BUS)
     */
    public String getType() {
        return type;
    }

    /**
     * Checks if the vehicle has a handicapped permit.
     *
     * @return True if the vehicle has a handicapped permit, false otherwise
     */
    public boolean hasHandicappedPermit() {
        return handicappedPermit;
    }

    /**
     * Sets the handicapped permit status of the vehicle.
     *
     * @param handicappedPermit Whether the vehicle has a handicapped permit
     */
    public void setHandicappedPermit(boolean handicappedPermit) {
        this.handicappedPermit = handicappedPermit;
    }
}