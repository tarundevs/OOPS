package com.parkinglot.system.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a parking spot in a parking lot, capable of holding a vehicle or a reservation.
 * Each spot has a unique ID, a specific type (e.g., car, bike, electric), and tracks its availability.
 * Implements Serializable to allow the spot's state to be saved or transmitted.
 */
public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    // Constants for parking spot types
    public static final String TYPE_CAR = "CAR";
    public static final String TYPE_BIKE = "BIKE";
    public static final String TYPE_TRUCK = "TRUCK";
    public static final String TYPE_ELECTRIC = "ELECTRIC";
    public static final String TYPE_HANDICAPPED = "HANDICAPPED";

    private String spotId;
    private String type;
    private boolean available;
    private Vehicle occupyingVehicle;
    private Reservation currentReservation;

    /**
     * Constructs a new parking spot with a given ID and type.
     * The spot starts as available with no vehicle or reservation.
     *
     * @param spotId The unique identifier for the spot
     * @param type   The type of parking spot (e.g., CAR, BIKE)
     * @throws IllegalArgumentException If the type is invalid
     */
    public ParkingSpot(String spotId, String type) {
        validateType(type);
        this.spotId = spotId;
        this.type = type;
        this.available = true;
        this.occupyingVehicle = null;
        this.currentReservation = null;
    }

    /**
     * Validates the parking spot type.
     *
     * @param type The type to validate
     * @throws IllegalArgumentException If the type is invalid
     */
    private void validateType(String type) {
        if (type == null || !(type.equals(TYPE_CAR) || type.equals(TYPE_BIKE) || type.equals(TYPE_TRUCK) ||
                              type.equals(TYPE_ELECTRIC) || type.equals(TYPE_HANDICAPPED))) {
            throw new IllegalArgumentException("Invalid parking spot type: " + type);
        }
    }

    /**
     * Checks if the parking spot is available for use.
     * A spot is available if it's not occupied and has no active reservation.
     *
     * @return True if the spot is available, false otherwise
     */
    public boolean isAvailable() {
        return available && currentReservation == null;
    }

    /**
     * Determines if a vehicle can park in this spot based on the spot's type and vehicle properties.
     *
     * @param vehicleType The type of vehicle
     * @return True if the vehicle can park in this spot, false otherwise
     */
    public boolean canFit(String vehicleType) {
    	Vehicle.validateVehicleType(vehicleType);
        return switch (type) {
            case TYPE_CAR -> vehicleType.equals(Vehicle.TYPE_CAR) || vehicleType.equals(Vehicle.TYPE_BIKE);
            case TYPE_BIKE -> vehicleType.equals(Vehicle.TYPE_BIKE);
            case TYPE_TRUCK -> true; // Truck spots can accommodate any vehicle
            case TYPE_ELECTRIC -> vehicleType.equals(Vehicle.TYPE_CAR);
            case TYPE_HANDICAPPED -> vehicleType.equals(Vehicle.TYPE_CAR) || vehicleType.equals(Vehicle.TYPE_BIKE);
            default -> false;
        };
    }

    /**
     * Marks the spot as occupied by a vehicle.
     * Sets the spot as unavailable and assigns the vehicle to it.
     *
     * @param vehicle The vehicle occupying the spot
     */
    public void parkVehicle(Vehicle vehicle) {
        this.available = false;
        this.occupyingVehicle = vehicle;
    }

    /**
     * Releases the spot, making it available again.
     * Clears the occupying vehicle.
     */
    public void removeVehicle() {
        this.available = true;
        this.occupyingVehicle = null;
        if (currentReservation != null && !currentReservation.getStatus().equals(Reservation.STATUS_CHECKED_IN)) {
            currentReservation = null;
        }
    }

    /**
     * Assigns a reservation to the spot.
     *
     * @param reservation The reservation to assign
     */
    public void reserve(Reservation reservation) {
        this.currentReservation = reservation;
        this.available = false;
    }

    /**
     * Cancels the current reservation, freeing the spot for other uses.
     */
    public void cancelReservation() {
        this.currentReservation = null;
        if (this.occupyingVehicle == null) {
            this.available = true;
        }
    }

    /**
     * Checks if the spot is available for a reservation during the specified time period.
     *
     * @param startTime The start time of the reservation
     * @param endTime   The end time of the reservation
     * @return True if the spot is available, false otherwise
     */
    public boolean isAvailableForReservation(Date startTime, Date endTime) {
        if (currentReservation == null) {
            return isAvailable();
        }
        return currentReservation.getEndTime().before(startTime) || currentReservation.getStartTime().after(endTime);
    }

    /**
     * Returns the unique ID of the parking spot.
     *
     * @return The spot ID
     */
    public String getSpotId() {
        return spotId;
    }

    /**
     * Returns the type of the parking spot.
     *
     * @return The spot type (e.g., CAR, BIKE)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the vehicle currently occupying the spot, if any.
     *
     * @return The occupying vehicle, or null if the spot is empty
     */
    public Vehicle getOccupyingVehicle() {
        return occupyingVehicle;
    }

    /**
     * Returns the current reservation for the spot, if any.
     *
     * @return The current reservation, or null if none exists
     */
    public Reservation getCurrentReservation() {
        return currentReservation;
    }
}