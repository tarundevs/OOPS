package com.parkinglot.system.payment;

/**
 * Interface for logging transaction details.
 */
public interface Loggable {
    /**
     * Logs the transaction details to a specified destination.
     *
     * @param transactionDetails The details of the transaction
     */
    void logTransaction(String transactionDetails);
}