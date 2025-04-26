package com.parkinglot.system.payment;
/**
 * Defines a contract for processing payment transactions in the parking lot system.
 * Implementations handle specific payment methods, such as credit cards or UPI.
 */
public interface PaymentMethod {

    /**
     * Processes a payment transaction for the specified amount.
     * Implementations should connect to a payment gateway or simulate the transaction.
     *
     * @param amount The amount to be charged
     * @return True if the transaction is successful, false otherwise
     */
    boolean processTransaction(double amount);

    /**
     * Retrieves details about the payment method, such as card number or UPI ID.
     * Useful for logging or displaying transaction information.
     *
     * @return A string containing the payment method details
     */
    String getPaymentDetails();
}