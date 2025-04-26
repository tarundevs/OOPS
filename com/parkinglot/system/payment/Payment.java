package com.parkinglot.system.payment;

import com.parkinglot.system.model.Vehicle;
import com.parkinglot.system.model.EntryExitLog;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a payment transaction for a parking session in the parking lot system.
 * Tracks the payment amount, vehicle, parking session details, and payment status.
 * Implements Serializable to allow the payment state to be saved or transmitted.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double amount; // Changed to wrapper class Double
    private Vehicle vehicle;
    private EntryExitLog parkingSession;
    private Date paymentTime;
    private PaymentStatus status;
    private PaymentMethod method;

    /**
     * Constructs a new payment for a parking session.
     * Initializes the payment as pending with the current timestamp.
     *
     * @param amount         The payment amount
     * @param vehicle        The vehicle being charged
     * @param parkingSession The parking session log
     */
    public Payment(Double amount, Vehicle vehicle, EntryExitLog parkingSession) {
        this.amount = amount != null ? amount : 0.0; // Handle null amount
        this.vehicle = vehicle;
        this.parkingSession = parkingSession;
        this.paymentTime = new Date();
        this.status = PaymentStatus.PENDING;
        this.method = null;
    }

    /**
     * Processes the payment using the specified payment method.
     * Simulates interaction with a payment gateway and updates the payment status.
     *
     * @param method The payment method to use (e.g., Credit Card, UPI)
     * @return True if the payment is successful, false otherwise
     */
    public boolean processPayment(PaymentMethod method) {
        this.method = method;
        if (amount == null) {
            System.out.println("Error: Payment amount is not specified.");
            this.status = PaymentStatus.FAILED;
            return false;
        }
        boolean success = method.processTransaction(amount);
        this.status = success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
        return success;
    }

    /**
     * Returns the payment amount.
     *
     * @return The amount to be paid
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Returns the vehicle associated with the payment.
     *
     * @return The vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the parking session log for this payment.
     *
     * @return The parking session log
     */
    public EntryExitLog getParkingSession() {
        return parkingSession;
    }

    /**
     * Returns the timestamp when the payment was initiated.
     *
     * @return The payment timestamp
     */
    public Date getPaymentTime() {
        return paymentTime;
    }

    /**
     * Returns the current status of the payment.
     *
     * @return The payment status (e.g., PENDING, COMPLETED)
     */
    public PaymentStatus getStatus() {
        return status;
    }

    /**
     * Returns the payment method used for the transaction.
     *
     * @return The payment method, or null if not yet processed
     */
    public PaymentMethod getMethod() {
        return method;
    }
}

/**
 * Enum defining the possible statuses of a payment transaction.
 */
enum PaymentStatus {
    PENDING,    // Payment is initiated but not yet processed
    COMPLETED,  // Payment was successfully processed
    FAILED,     // Payment processing failed
    REFUNDED    // Payment was refunded to the customer
}