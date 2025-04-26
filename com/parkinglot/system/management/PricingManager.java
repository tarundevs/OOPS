package com.parkinglot.system.management;

import com.parkinglot.system.model.ParkingSpot;
import com.parkinglot.system.model.Vehicle;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages pricing calculations for parking fees in the parking lot system.
 * Calculates fees based on vehicle type, parking spot type, duration, and dynamic factors like peak hours or weekends.
 * Implements Serializable to allow the pricing configuration to be saved or transmitted.
 */
public class PricingManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Double> baseRates;
    private Map<String, Double> spotTypeMultipliers;
    private Double peakHourSurcharge;
    private Double weekendSurcharge;

    /**
     * Constructs a PricingManager with default pricing rates and surcharges.
     */
    public PricingManager() {
        initializeDefaultRates();
    }

    /**
     * Constructs a PricingManager with specified base rates and spot type multipliers.
     *
     * @param baseRates         The base rates for different vehicle types
     * @param spotTypeMultipliers The multipliers for different parking spot types
     */
    public PricingManager(Map<String, Double> baseRates, Map<String, Double> spotTypeMultipliers) {
        this.baseRates = new HashMap<>(baseRates);
        this.spotTypeMultipliers = new HashMap<>(spotTypeMultipliers);
        this.peakHourSurcharge = 1.5;
        this.weekendSurcharge = 1.2;
    }

    /**
     * Initializes default pricing rates and multipliers for vehicle and spot types.
     */
    private void initializeDefaultRates() {
        baseRates = new HashMap<>();
        baseRates.put(Vehicle.TYPE_CAR, 40.0);
        baseRates.put(Vehicle.TYPE_BIKE, 20.0);
        baseRates.put(Vehicle.TYPE_TRUCK, 80.0);
        baseRates.put(Vehicle.TYPE_BUS, 100.0);

        spotTypeMultipliers = new HashMap<>();
        spotTypeMultipliers.put(ParkingSpot.TYPE_CAR, 1.0);
        spotTypeMultipliers.put(ParkingSpot.TYPE_BIKE, 0.5);
        spotTypeMultipliers.put(ParkingSpot.TYPE_TRUCK, 1.5);
        spotTypeMultipliers.put(ParkingSpot.TYPE_ELECTRIC, 1.2);
        spotTypeMultipliers.put(ParkingSpot.TYPE_HANDICAPPED, 0.8);

        peakHourSurcharge = 1.5;
        weekendSurcharge = 1.2;
    }

    /**
     * Calculates the parking fee based on vehicle type, parking duration, and spot type.
     *
     * @param vehicleType The type of vehicle
     * @param hours       The duration of parking in hours
     * @param spotType    The type of parking spot
     * @return The calculated parking fee
     * @throws IllegalArgumentException If vehicle type, spot type, or duration is invalid
     */
    public Double calculateFee(String vehicleType, Double hours, String spotType) {
        validateType(vehicleType, spotType);
        Double baseRate = baseRates.getOrDefault(vehicleType, 50.0);
        Double spotMultiplier = spotTypeMultipliers.getOrDefault(spotType, 1.0);
        Double duration = hours != null ? hours : 0.0;
        if (duration < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative");
        }
        Double fee = baseRate * duration * spotMultiplier;

        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        boolean isPeakHour = (hour >= 8 && hour <= 10) || (hour >= 17 && hour <= 19);
        boolean isWeekend = now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY;

        if (isPeakHour && !isWeekend) {
            fee *= peakHourSurcharge;
        } else if (isWeekend) {
            fee *= weekendSurcharge;
        }

        return fee;
    }

    /**
     * Validates the vehicle type and spot type for pricing calculations.
     *
     * @param vehicleType The type of vehicle
     * @param spotType    The type of parking spot
     * @throws IllegalArgumentException If vehicle type or spot type is invalid
     */
    private void validateType(String vehicleType, String spotType) {
        if (vehicleType == null || !(vehicleType.equals(Vehicle.TYPE_CAR) || vehicleType.equals(Vehicle.TYPE_BIKE) ||
                                     vehicleType.equals(Vehicle.TYPE_TRUCK) || vehicleType.equals(Vehicle.TYPE_BUS))) {
            throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }
        if (spotType == null || !(spotType.equals(ParkingSpot.TYPE_CAR) || spotType.equals(ParkingSpot.TYPE_BIKE) ||
                                  spotType.equals(ParkingSpot.TYPE_TRUCK) || spotType.equals(ParkingSpot.TYPE_ELECTRIC) ||
                                  spotType.equals(ParkingSpot.TYPE_HANDICAPPED))) {
            throw new IllegalArgumentException("Invalid spot type: " + spotType);
        }
    }

    /**
     * Calculates the parking fee for a variable number of hours.
     *
     * @param vehicleType The type of vehicle
     * @param spotType    The type of parking spot
     * @param hours       Variable number of hours to sum for the total duration
     * @return The calculated parking fee
     */
    public Double calculateFee(String vehicleType, String spotType, Double... hours) {
        Double totalHours = 0.0;
        for (Double hour : hours) {
            totalHours += hour != null ? hour : 0.0;
        }
        return calculateFee(vehicleType, totalHours, spotType);
    }

    /**
     * Calculates the monthly subscription fee based on a daily rate for a vehicle and spot type.
     *
     * @param vehicleType The type of vehicle
     * @param spotType    The type of parking spot
     * @return The calculated monthly subscription fee
     */
    public double calculateMonthlySubscriptionFee(String vehicleType, String spotType) {
        Double dailyRate = calculateFee(vehicleType, 8.0, spotType);
        return dailyRate * 22 * 0.7;
    }

    /**
     * Calculates parking fees for multiple vehicle types for a given duration and spot type.
     *
     * @param hours        The duration of parking in hours
     * @param spotType     The type of parking spot
     * @param vehicleTypes The types of vehicles
     * @return An array of calculated fees for each vehicle type
     */
    public Double[] calculateFeesForTypes(Double hours, String spotType, String... vehicleTypes) {
        Double[] fees = new Double[vehicleTypes.length];
        for (int i = 0; i < vehicleTypes.length; i++) {
            fees[i] = calculateFee(vehicleTypes[i], hours, spotType);
        }
        return fees;
    }

    /**
     * Sets the base rate for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle
     * @param rate        The base rate to set
     * @throws IllegalArgumentException If the vehicle type is invalid
     */
    public void setBaseRate(String vehicleType, Double rate) {
        validateType(vehicleType, ParkingSpot.TYPE_CAR);
        baseRates.put(vehicleType, rate);
    }

    /**
     * Sets the multiplier for a specific parking spot type.
     *
     * @param spotType   The type of parking spot
     * @param multiplier The multiplier to set
     * @throws IllegalArgumentException If the spot type is invalid
     */
    public void setSpotTypeMultiplier(String spotType, Double multiplier) {
        validateType(Vehicle.TYPE_CAR, spotType);
        spotTypeMultipliers.put(spotType, multiplier);
    }

    /**
     * Sets the surcharge applied during peak hours.
     *
     * @param surcharge The peak hour surcharge to set
     */
    public void setPeakHourSurcharge(Double surcharge) {
        this.peakHourSurcharge = surcharge;
    }

    /**
     * Sets the surcharge applied during weekends.
     *
     * @param surcharge The weekend surcharge to set
     */
    public void setWeekendSurcharge(Double surcharge) {
        this.weekendSurcharge = surcharge;
    }

    /**
     * Retrieves the base rate for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle
     * @return The base rate, or 0.0 if not found
     */
    public Double getBaseRate(String vehicleType) {
        return baseRates.getOrDefault(vehicleType, 0.0);
    }

    /**
     * Retrieves the multiplier for a specific parking spot type.
     *
     * @param spotType The type of parking spot
     * @return The multiplier, or 1.0 if not found
     */
    public Double getSpotTypeMultiplier(String spotType) {
        return spotTypeMultipliers.getOrDefault(spotType, 1.0);
    }
}