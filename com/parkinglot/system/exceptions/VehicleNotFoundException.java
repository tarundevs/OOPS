package com.parkinglot.system.exceptions;
import java.io.Serializable;

/**
 * Custom exception thrown when a vehicle is not found in the system.
 * Implements Serializable to support persistence.
 */
public class VehicleNotFoundException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a VehicleNotFoundException with the specified message.
     *
     * @param message The detail message explaining why the vehicle was not found
     */
    public VehicleNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a VehicleNotFoundException with the specified message and cause.
     *
     * @param Alphabetic message The detail message explaining why the vehicle was not found
     * @param cause      The underlying cause of the exception
     */
    public VehicleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}