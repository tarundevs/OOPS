package com.parkinglot.system.payment;

import java.io.Serializable;

/**
 * Represents a Credit Card payment method for processing transactions.
 * Implements PaymentMethod and Loggable to support payment processing and transaction logging.
 */
public class CreditCardPayment implements PaymentMethod, Loggable, Serializable {
    private static final long serialVersionUID = 1L;

    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardHolderName;

    /**
     * Creates a CreditCardPayment with the specified details.
     *
     * @param cardNumber     The credit card number
     * @param expiryDate     The expiry date (MM/YY)
     * @param cvv            The CVV code
     * @param cardHolderName The cardholder's name
     */
    public CreditCardPayment(String cardNumber, String expiryDate, String cvv, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardHolderName = cardHolderName;
    }

    /**
     * Processes a credit card transaction for the specified amount.
     * Logs the transaction details upon processing.
     *
     * @param amount The transaction amount
     * @return true if the transaction is successful, false otherwise
     */
    @Override
    public boolean processTransaction(double amount) {
        System.out.println("Processing credit card payment of Rs. " + amount);
        String details = "Credit card payment of Rs. " + amount + " processed for card ending in " +
                         cardNumber.substring(cardNumber.length() - 4);
        logTransaction(details);
        return isValidCard();
    }

    /**
     * Retrieves the payment details for this credit card.
     *
     * @return A string containing the masked card number
     */
    @Override
    public String getPaymentDetails() {
        return "Credit Card: **** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Logs the transaction details to the console.
     * Can be extended to log to a file or database.
     *
     * @param transactionDetails The details of the transaction
     */
    @Override
    public void logTransaction(String transactionDetails) {
        System.out.println("Transaction Log: " + transactionDetails);
        // Future extension: Write to a log file or database
    }

    /**
     * Validates the credit card details.
     *
     * @return true if the card is valid, false otherwise
     */
    private boolean isValidCard() {
        return cardNumber != null && cardNumber.length() == 16 && cvv != null && cvv.length() == 3;
    }
}