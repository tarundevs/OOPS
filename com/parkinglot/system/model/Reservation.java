package com.parkinglot.system.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a reservation for a parking spot in the parking lot system.
 * Tracks the parking spot, vehicle, reservation period, and status.
 * Implements Serializable to support persistence.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    // Constants for reservation status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CHECKED_IN = "CHECKED_IN";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private ParkingSpot spot;
    private Vehicle vehicle;
    private Date startTime;
    private Date endTime;
    private String status;

    /**
     * Constructs a reservation for a parking spot.
     *
     * @param spot      The parking spot reserved
     * @param vehicle   The vehicle for the reservation
     * @param startTime The start time of the reservation
     * @param endTime   The end time of the reservation
     */
    public Reservation(ParkingSpot spot, Vehicle vehicle, Date startTime, Date endTime) {
        this.spot = spot;
        this.vehicle = vehicle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = STATUS_PENDING;
    }

    /**
     * Checks if the reservation is active.
     *
     * @return True if the reservation is pending or checked in, false otherwise
     */
    public boolean isActive() {
        Date now = new Date();
        return (status.equals(STATUS_PENDING) || status.equals(STATUS_CHECKED_IN)) &&
               now.after(startTime) && now.before(endTime);
    }

    /**
     * Sets the status of the reservation.
     *
     * @param status The new status (e.g., CHECKED_IN, COMPLETED)
     * @throws IllegalArgumentException If the status is invalid
     */
    public void setStatus(String status) {
        validateStatus(status);
        this.status = status;
    }

    /**
     * Validates the reservation status.
     *
     * @param status The status to validate
     * @throws IllegalArgumentException If the status is invalid
     */
    private void validateStatus(String status) {
        if (status == null || !(status.equals(STATUS_PENDING) || status.equals(STATUS_CHECKED_IN) ||
                                status.equals(STATUS_COMPLETED) || status.equals(STATUS_CANCELLED))) {
            throw new IllegalArgumentException("Invalid reservation status: " + status);
        }
    }

    /**
     * Returns the parking spot for the reservation.
     *
     * @return The parking spot
     */
    public ParkingSpot getSpot() {
        return spot;
    }

    /**
     * Returns the vehicle for the reservation.
     *
     * @return The vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the start time of the reservation.
     *
     * @return The start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the reservation.
     *
     * @return The end time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Returns the status of the reservation.
     *
     * @return The status
     */
    public String getStatus() {
        return status;
    }
}