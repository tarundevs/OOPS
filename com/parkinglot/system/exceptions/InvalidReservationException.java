package com.parkinglot.system.exceptions;
import java.io.Serializable;

/**
 * A custom exception thrown when a parking reservation is invalid.
 * This exception helps handle errors like invalid dates, unavailable spots, or incorrect reservation details.
 * Implements Serializable to allow the exception to be saved or transmitted.
 */
public class InvalidReservationException extends Exception implements Serializable {

    // Unique identifier for serialization, ensuring compatibility during deserialization
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an InvalidReservationException with a specific error message.
     *
     * @param message A description of what went wrong with the reservation
     */
    public InvalidReservationException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidReservationException with an error message and a cause.
     * Useful for wrapping other exceptions to provide more context.
     *
     * @param message A description of what went wrong with the reservation
     * @param cause   The underlying exception that triggered this one
     */
    public InvalidReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}