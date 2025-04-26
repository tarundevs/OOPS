package com.parkinglot.system.payment;
import java.io.Serializable;

/**
 * Represents a UPI (Unified Payments Interface) payment method for processing transactions.
 * Implements the PaymentMethod interface and is serializable for persistence.
 */
public class UPIPayment implements PaymentMethod, Serializable {
    private static final long serialVersionUID = 1L;

    // The UPI ID used for transactions (e.g., user@bank)
    private final String upiId;

    /**
     * Creates a UPIPayment instance with the specified UPI ID.
     *
     * @param upiId The UPI ID for the payment method
     */
    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    /**
     * Processes a UPI transaction for the specified amount.
     * Currently simulates a connection to a UPI gateway and validates the UPI ID.
     *
     * @param amount The transaction amount in dollars
     * @return true if the transaction is successful, false otherwise
     */
    @Override
    public boolean processTransaction(double amount) {
        // Log the transaction attempt (in a real application, this would connect to a UPI gateway)
        System.out.println("Processing UPI payment of Rs. " + amount);

        // Simple validation: checks if UPI ID contains "@" (e.g., user@bank)
        return upiId != null && upiId.contains("@");
    }

    /**
     * Retrieves the payment details for this UPI payment method.
     *
     * @return A string containing the UPI ID
     */
    @Override
    public String getPaymentDetails() {
        return "UPI ID: " + upiId;
    }
}