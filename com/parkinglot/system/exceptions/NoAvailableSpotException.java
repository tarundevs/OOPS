package com.parkinglot.system.exceptions;
import java.io.Serializable;

/**
 * A custom exception thrown when no parking spots are available in the parking lot.
 * This exception helps handle situations where a vehicle cannot be parked due to full occupancy.
 * Implements Serializable to allow the exception to be saved or transmitted.
 */
public class NoAvailableSpotException extends Exception implements Serializable {

    // Unique identifier for serialization, ensuring compatibility during deserialization
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NoAvailableSpotException with a specific error message.
     *
     * @param message A description of why no parking spots are available
     */
    public NoAvailableSpotException(String message) {
        super(message);
    }

    /**
     * Constructs a NoAvailableSpotException with an error message and a cause.
     * Useful for wrapping other exceptions to provide additional context about the failure.
     *
     * @param message A description of why no parking spots are available
     * @param cause   The underlying exception that triggered this one
     */
    public NoAvailableSpotException(String message, Throwable cause) {
        super(message, cause);
    }
}