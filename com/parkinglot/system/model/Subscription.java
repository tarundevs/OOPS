package com.parkinglot.system.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a parking subscription for a vehicle in the parking lot system.
 * Tracks the vehicle, subscription period, type, and spot type.
 * Implements Serializable to support persistence.
 */
public class Subscription implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_MONTHLY = "MONTHLY";
    public static final String TYPE_QUARTERLY = "QUARTERLY";
    public static final String TYPE_SEMI_ANNUAL = "SEMI_ANNUAL";
    public static final String TYPE_ANNUAL = "ANNUAL";

    private String subscriptionId;
    private Vehicle vehicle;
    private Date startDate;
    private Date endDate;
    private String type;
    private String spotType;
    private boolean active;

    /**
     * Constructs a Subscription for a vehicle with specified details.
     *
     * @param vehicle   The vehicle associated with the subscription
     * @param startDate The start date of the subscription
     * @param endDate   The end date of the subscription
     * @param type      The type of subscription (e.g., MONTHLY, QUARTERLY)
     * @param spotType  The type of parking spot associated with the subscription
     * @throws IllegalArgumentException If the subscription type is invalid
     */
    public Subscription(Vehicle vehicle, Date startDate, Date endDate, String type, String spotType) {
        validateType(type);
        this.subscriptionId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.spotType = spotType;
        this.active = true;
    }

    /**
     * Validates the subscription type to ensure it is one of the allowed types.
     *
     * @param type The subscription type to validate
     * @throws IllegalArgumentException If the subscription type is invalid
     */
    private void validateType(String type) {
        if (type == null || !(type.equals(TYPE_MONTHLY) || type.equals(TYPE_QUARTERLY) ||
                              type.equals(TYPE_SEMI_ANNUAL) || type.equals(TYPE_ANNUAL))) {
            throw new IllegalArgumentException("Invalid subscription type: " + type);
        }
    }

    /**
     * Checks if the subscription is currently active based on its date range and status.
     *
     * @return True if the subscription is active, false otherwise
     */
    public boolean isActive() {
        Date now = new Date();
        return active && now.after(startDate) && now.before(endDate);
    }

    /**
     * Cancels the subscription by setting its active status to false.
     */
    public void cancel() {
        this.active = false;
    }

    /**
     * Renews the subscription by extending its end date by the specified number of months.
     *
     * @param months The number of months to extend the subscription
     */
    public void renew(int months) {
        if (active) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.MONTH, months);
            this.endDate = calendar.getTime();
        }
    }

    /**
     * Retrieves the unique identifier of the subscription.
     *
     * @return The subscription ID
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * Retrieves the vehicle associated with the subscription.
     *
     * @return The vehicle object
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Retrieves the start date of the subscription.
     *
     * @return The start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Retrieves the end date of the subscription.
     *
     * @return The end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Retrieves the type of the subscription.
     *
     * @return The subscription type (e.g., MONTHLY, QUARTERLY)
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the type of parking spot associated with the subscription.
     *
     * @return The parking spot type
     */
    public String getSpotType() {
        return spotType;
    }
}