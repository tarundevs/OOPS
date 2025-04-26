package com.parkinglot.system.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a log entry for a vehicle's entry and exit in the parking lot.
 * Tracks the vehicle, parking spot, entry time, and exit time.
 * Implements Serializable to support persistence.
 */
public class EntryExitLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Vehicle vehicle;
    private ParkingSpot spot;
    private Date entryTime;
    private Date exitTime;

    /**
     * Constructs an entry-exit log for a vehicle.
     *
     * @param vehicle   The vehicle entering the parking lot
     * @param spot      The assigned parking spot
     * @param entryTime The time of entry
     */
    public EntryExitLog(Vehicle vehicle, ParkingSpot spot, Date entryTime) {
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
        this.exitTime = null;
    }

    /**
     * Sets the exit time for the vehicle.
     *
     * @param exitTime The time of exit
     */
    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    /**
     * Calculates the parking duration in hours.
     *
     * @return The duration in hours, or 0.0 if exit time is not set
     */
    public Double getParkingDuration() {
        if (exitTime == null || entryTime == null) {
            return 0.0;
        }
        long durationMillis = exitTime.getTime() - entryTime.getTime();
        return durationMillis / (1000.0 * 60 * 60); // Convert milliseconds to hours
    }

    /**
     * Returns the vehicle associated with the log.
     *
     * @return The vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the parking spot associated with the log.
     *
     * @return The parking spot
     */
    public ParkingSpot getSpot() {
        return spot;
    }

    /**
     * Returns the entry time.
     *
     * @return The entry time
     */
    public Date getEntryTime() {
        return entryTime;
    }

    /**
     * Returns the exit time.
     *
     * @return The exit time, or null if not set
     */
    public Date getExitTime() {
        return exitTime;
    }
}