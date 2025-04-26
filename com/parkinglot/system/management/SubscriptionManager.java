package com.parkinglot.system.management;

import com.parkinglot.system.model.Subscription;
import com.parkinglot.system.model.Vehicle;
import com.parkinglot.system.payment.Payment;
import com.parkinglot.system.payment.PaymentMethod;

import java.io.Serializable;
import java.util.*;

/**
 * Manages parking subscriptions for vehicles in the parking lot system.
 * Handles subscription creation, renewal, cancellation, and payment processing.
 * Implements Serializable to support persistence.
 */
public class SubscriptionManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Subscription> subscriptions;
    private PricingManager pricingManager;
    private ParkingLot parkingLot; // Reference to ParkingLot for transaction logging

    /**
     * Constructs a SubscriptionManager with a PricingManager and ParkingLot.
     *
     * @param pricingManager The pricing manager for calculating fees
     * @param parkingLot     The parking lot for logging transactions
     */
    public SubscriptionManager(PricingManager pricingManager, ParkingLot parkingLot) {
        this.subscriptions = new HashMap<>();
        this.pricingManager = pricingManager;
        this.parkingLot = parkingLot;
    }

    /**
     * Registers a new subscription for a vehicle.
     *
     * @param vehicle   The vehicle to subscribe
     * @param type      The subscription type (e.g., MONTHLY, ANNUAL)
     * @param spotType  The parking spot type (e.g., CAR, ELECTRIC)
     * @param paymentMethod The payment method for the subscription
     * @return True if the subscription is successfully registered, false otherwise
     */
    public boolean registerSubscription(Vehicle vehicle, String type, String spotType, PaymentMethod paymentMethod) {
        double fee = calculateSubscriptionFee(vehicle, type, spotType);
        Payment payment = new Payment(fee, vehicle, null);
        boolean paymentSuccess = payment.processPayment(paymentMethod);

        if (paymentSuccess) {
            Calendar calendar = Calendar.getInstance();
            Date startDate = new Date();
            calendar.setTime(startDate);
            int months = switch (type) {
                case Subscription.TYPE_QUARTERLY -> 3;
                case Subscription.TYPE_SEMI_ANNUAL -> 6;
                case Subscription.TYPE_ANNUAL -> 12;
                default -> 1; // MONTHLY
            };
            calendar.add(Calendar.MONTH, months);
            Date endDate = calendar.getTime();

            Subscription subscription = new Subscription(vehicle, startDate, endDate, type, spotType);
            subscriptions.put(vehicle.getLicensePlate(), subscription);
            parkingLot.getAllTransactions().add(payment); // Add payment to transactions
            parkingLot.logTransaction("Subscription registered for vehicle: " + vehicle.getLicensePlate() +
                                      " type: " + type + " amount: Rs. " + fee);
        }
        return paymentSuccess;
    }

    /**
     * Calculates the subscription fee for a vehicle.
     *
     * @param vehicle   The vehicle
     * @param type      The subscription type
     * @param spotType  The parking spot type
     * @return The subscription fee
     */
    public double calculateSubscriptionFee(Vehicle vehicle, String type, String spotType) {
        double baseFee = pricingManager.calculateMonthlySubscriptionFee(vehicle.getType(), spotType);
        return switch (type) {
            case Subscription.TYPE_QUARTERLY -> baseFee * 3 * 0.9; // 10% discount
            case Subscription.TYPE_SEMI_ANNUAL -> baseFee * 6 * 0.85; // 15% discount
            case Subscription.TYPE_ANNUAL -> baseFee * 12 * 0.8; // 20% discount
            default -> baseFee; // MONTHLY
        };
    }

    /**
     * Processes payment for a subscription.
     *
     * @param vehicle   The vehicle
     * @param type      The subscription type
     * @param spotType  The parking spot type
     * @param paymentMethod The payment method
     * @return True if payment is successful, false otherwise
     */
    public boolean processSubscriptionPayment(Vehicle vehicle, String type, String spotType, PaymentMethod paymentMethod) {
        boolean success = registerSubscription(vehicle, type, spotType, paymentMethod);
        if (!success) {
            parkingLot.logTransaction("Subscription payment failed for vehicle: " + vehicle.getLicensePlate() +
                                      " type: " + type + " amount: Rs. " + calculateSubscriptionFee(vehicle, type, spotType));
        }
        return success;
    }

    /**
     * Renews a subscription by extending its end date.
     *
     * @param licensePlate The vehicle's license plate
     * @param months       The number of months to extend
     */
    public void renewSubscription(String licensePlate, int months) {
        Subscription subscription = subscriptions.get(licensePlate.toUpperCase());
        if (subscription != null && subscription.isActive()) {
            subscription.renew(months);
            parkingLot.logTransaction("Subscription renewed for vehicle: " + licensePlate + " for " + months + " months");
        }
    }

    /**
     * Cancels a subscription.
     *
     * @param licensePlate The vehicle's license plate
     */
    public void cancelSubscription(String licensePlate) {
        Subscription subscription = subscriptions.get(licensePlate.toUpperCase());
        if (subscription != null) {
            subscription.cancel();
            parkingLot.logTransaction("Subscription cancelled for vehicle: " + licensePlate);
        }
    }

    /**
     * Checks if a vehicle has an active subscription.
     *
     * @param licensePlate The vehicle's license plate
     * @return True if the vehicle has an active subscription, false otherwise
     */
    public boolean hasActiveSubscription(String licensePlate) {
        Subscription subscription = subscriptions.get(licensePlate.toUpperCase());
        return subscription != null && subscription.isActive();
    }

    /**
     * Retrieves a subscription by license plate.
     *
     * @param licensePlate The vehicle's license plate
     * @return The subscription, or null if not found
     */
    public Subscription getSubscription(String licensePlate) {
        return subscriptions.get(licensePlate.toUpperCase());
    }

    /**
     * Retrieves all active subscriptions.
     *
     * @return A list of active subscriptions
     */
    public List<Subscription> getAllActiveSubscriptions() {
        List<Subscription> activeSubscriptions = new ArrayList<>();
        for (Subscription sub : subscriptions.values()) {
            if (sub.isActive()) {
                activeSubscriptions.add(sub);
            }
        }
        return activeSubscriptions;
    }
}