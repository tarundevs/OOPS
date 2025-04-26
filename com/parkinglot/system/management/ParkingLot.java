package com.parkinglot.system.management;

import com.parkinglot.system.model.*;
import com.parkinglot.system.payment.Payment;
import com.parkinglot.system.payment.Loggable;
import com.parkinglot.system.exceptions.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Represents a parking lot with multiple parking spots, managing vehicle check-in, check-out, and reservations.
 * Implements Serializable to support persistence.
 */
public class ParkingLot implements Serializable, Loggable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<ParkingSpot> spots;
    private Map<String, EntryExitLog> activeVehicles;
    private List<Reservation> reservations;
    private PricingManager pricingManager;
    private SubscriptionManager subscriptionManager;
    private transient SimpleDateFormat dateFormat;
    private Map<String, List<EntryExitLog>> securityLogs;
    private List<Payment> transactions;

    /**
     * Constructs a ParkingLot with a specified name and total number of spots.
     *
     * @param name       The name of the parking lot
     * @param totalSpots The total number of parking spots
     */
    public ParkingLot(String name, int totalSpots) {
        this.name = name;
        this.spots = new ArrayList<>();
        this.activeVehicles = new HashMap<>();
        this.reservations = new ArrayList<>();
        this.pricingManager = new PricingManager();
        this.subscriptionManager = new SubscriptionManager(pricingManager, this);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.securityLogs = new HashMap<>();
        this.transactions = new ArrayList<>();
        initializeSpots(totalSpots);
    }

    /**
     * Initializes parking spots based on the total number of spots, distributing them across different types.
     *
     * @param totalSpots The total number of parking spots to initialize
     */
    private void initializeSpots(int totalSpots) {
        int carSpots = totalSpots / 2;
        int bikeSpots = totalSpots / 4;
        int truckSpots = totalSpots / 8;
        int electricSpots = totalSpots / 8;
        int handicappedSpots = totalSpots - carSpots - bikeSpots - truckSpots - electricSpots;

        for (int i = 0; i < carSpots; i++) {
            spots.add(new ParkingSpot("C" + (i + 1), ParkingSpot.TYPE_CAR));
        }
        for (int i = 0; i < bikeSpots; i++) {
            spots.add(new ParkingSpot("B" + (i + 1), ParkingSpot.TYPE_BIKE));
        }
        for (int i = 0; i < truckSpots; i++) {
            spots.add(new ParkingSpot("T" + (i + 1), ParkingSpot.TYPE_TRUCK));
        }
        for (int i = 0; i < electricSpots; i++) {
            spots.add(new ParkingSpot("E" + (i + 1), ParkingSpot.TYPE_ELECTRIC));
        }
        for (int i = 0; i < handicappedSpots; i++) {
            spots.add(new ParkingSpot("H" + (i + 1), ParkingSpot.TYPE_HANDICAPPED));
        }
    }

    /**
     * Checks in a vehicle to the parking lot, assigning it to an available spot.
     *
     * @param vehicle The vehicle to check in
     * @return The assigned parking spot
     * @throws NoAvailableSpotException If no suitable spot is available
     */
    public ParkingSpot checkIn(Vehicle vehicle) throws NoAvailableSpotException {
        ParkingSpot spot = findAvailableSpot(vehicle.getType());
        if (spot == null) {
            throw new NoAvailableSpotException("No available spot for vehicle type: " + vehicle.getType());
        }
        spot.parkVehicle(vehicle);
        EntryExitLog log = new EntryExitLog(vehicle, spot, new Date());
        activeVehicles.put(vehicle.getLicensePlate(), log);
        securityLogs.computeIfAbsent(vehicle.getLicensePlate(), k -> new ArrayList<>()).add(log);
        logTransaction("Check-in for vehicle: " + vehicle.getLicensePlate() + " at spot: " + spot.getSpotId());
        return spot;
    }

    /**
     * Checks in a vehicle with a reservation, assigning it to the reserved spot.
     *
     * @param vehicle     The vehicle to check in
     * @param reservation The reservation associated with the vehicle
     * @return The assigned parking spot
     * @throws InvalidReservationException If the reservation is invalid or inactive
     */
    public ParkingSpot checkIn(Vehicle vehicle, Reservation reservation) throws InvalidReservationException {
        if (!reservation.isActive() || !reservation.getVehicle().getLicensePlate().equals(vehicle.getLicensePlate())) {
            throw new InvalidReservationException("Invalid or inactive reservation");
        }
        ParkingSpot spot = reservation.getSpot();
        spot.parkVehicle(vehicle);
        reservation.setStatus(Reservation.STATUS_CHECKED_IN);
        EntryExitLog log = new EntryExitLog(vehicle, spot, new Date());
        activeVehicles.put(vehicle.getLicensePlate(), log);
        securityLogs.computeIfAbsent(vehicle.getLicensePlate(), k -> new ArrayList<>()).add(log);
        logTransaction("Check-in with reservation for vehicle: " + vehicle.getLicensePlate() + " at spot: " + spot.getSpotId());
        return spot;
    }

    /**
     * Prepares the checkout process for a vehicle, calculating the parking fee.
     *
     * @param vehicle The vehicle to check out
     * @return The payment object containing the fee details
     * @throws VehicleNotFoundException If the vehicle is not found in the parking lot
     */
    public Payment prepareCheckOut(Vehicle vehicle) throws VehicleNotFoundException {
        EntryExitLog log = activeVehicles.get(vehicle.getLicensePlate());
        if (log == null) {
            throw new VehicleNotFoundException("Vehicle with license plate " + vehicle.getLicensePlate() + " not found");
        }
        log.setExitTime(new Date());
        Double hours = log.getParkingDuration();
        Double fee=0.0;
        if (!subscriptionManager.hasActiveSubscription(vehicle.getLicensePlate())) {
        	fee = pricingManager.calculateFee(vehicle.getType(), hours, log.getSpot().getType());
        } else {
            System.out.println("No checkout fee for vehicle: " + vehicle.getLicensePlate() + " due to active subscription");
        }
        Payment payment = new Payment(fee, vehicle, log);
        transactions.add(payment);
        return payment;
    }

    /**
     * Finalizes the checkout process for a vehicle, removing it from the parking lot if payment is successful.
     *
     * @param vehicle        The vehicle to check out
     * @param paymentSuccess Indicates whether the payment was successful
     */
    public void finalizeCheckOut(Vehicle vehicle, boolean paymentSuccess) {
        if (paymentSuccess) {
            EntryExitLog log = activeVehicles.get(vehicle.getLicensePlate());
            if (log != null) {
                log.getSpot().removeVehicle();
                activeVehicles.remove(vehicle.getLicensePlate());
                logTransaction("Check-out for vehicle: " + vehicle.getLicensePlate() + " with payment success");
            }
        } else {
            logTransaction("Check-out failed for vehicle: " + vehicle.getLicensePlate() + " due to payment failure");
        }
    }

    /**
     * Creates a reservation for a vehicle for a specified time period.
     *
     * @param vehicle   The vehicle to reserve a spot for
     * @param startTime The start time of the reservation
     * @param endTime   The end time of the reservation
     * @return The created reservation
     * @throws NoAvailableSpotException   If no suitable spot is available
     * @throws InvalidReservationException If the reservation time period is invalid
     */
    public Reservation makeReservation(Vehicle vehicle, Date startTime, Date endTime)
            throws NoAvailableSpotException, InvalidReservationException {
        if (startTime.after(endTime) || startTime.equals(endTime)) {
            throw new InvalidReservationException("Invalid reservation time period");
        }
        ParkingSpot spot = findAvailableSpotForReservation(vehicle.getType(), startTime, endTime);
        if (spot == null) {
            throw new NoAvailableSpotException("No available spot for reservation");
        }
        Reservation reservation = new Reservation(spot, vehicle, startTime, endTime);
        spot.reserve(reservation);
        reservations.add(reservation);
        logTransaction("Reservation made for vehicle: " + vehicle.getLicensePlate() + " from " + startTime + " to " + endTime);
        return reservation;
    }

    /**
     * Checks if a vehicle is currently parked in the parking lot.
     *
     * @param licensePlate The license plate of the vehicle
     * @return True if the vehicle is parked, false otherwise
     */
    public boolean isVehicleParked(String licensePlate) {
        return activeVehicles.containsKey(licensePlate.toUpperCase());
    }

    /**
     * Checks if a reservation conflicts with existing reservations or active vehicles.
     *
     * @param vehicle   The vehicle to check for conflicts
     * @param startTime The start time of the proposed reservation
     * @param endTime   The end time of the proposed reservation
     * @return True if there is a conflict, false otherwise
     */
    public boolean hasReservationConflict(Vehicle vehicle, Date startTime, Date endTime) {
        for (Reservation res : reservations) {
            if (res.getVehicle().getLicensePlate().equalsIgnoreCase(vehicle.getLicensePlate()) &&
                res.isActive() &&
                !(endTime.before(res.getStartTime()) || startTime.after(res.getEndTime()))) {
                return true;
            }
        }
        return activeVehicles.containsKey(vehicle.getLicensePlate());
    }

    /**
     * Finds an active reservation by the vehicle's license plate.
     *
     * @param licensePlate The license plate of the vehicle
     * @return The active reservation, or null if none is found
     */
    public Reservation findReservationByLicensePlate(String licensePlate) {
        for (Reservation res : reservations) {
            if (res.getVehicle().getLicensePlate().equalsIgnoreCase(licensePlate) &&
                res.isActive() &&
                res.getStatus().equals(Reservation.STATUS_PENDING)) {
                return res;
            }
        }
        return null;
    }

    /**
     * Cancels a reservation by the vehicle's license plate.
     *
     * @param licensePlate The license plate of the vehicle
     * @return True if the reservation was canceled, false if no reservation was found
     */
    public boolean cancelReservationByLicensePlate(String licensePlate) {
        Reservation reservation = findReservationByLicensePlate(licensePlate);
        if (reservation == null) {
            return false;
        }
        reservation.setStatus(Reservation.STATUS_CANCELLED);
        reservation.getSpot().cancelReservation();
        logTransaction("Reservation cancelled for vehicle: " + licensePlate);
        return true;
    }

    /**
     * Retrieves all active reservations in the parking lot.
     *
     * @return A list of active reservations
     */
    public List<Reservation> getAllActiveReservations() {
        List<Reservation> activeReservations = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.isActive()) {
                activeReservations.add(res);
            }
        }
        return activeReservations;
    }

    /**
     * Retrieves all security logs for vehicles in the parking lot.
     *
     * @return A map of vehicle license plates to their security logs
     */
    public Map<String, List<EntryExitLog>> getSecurityLogs() {
        return new HashMap<>(securityLogs);
    }

    /**
     * Retrieves security logs for a specific date.
     *
     * @param date The date to filter logs by
     * @return A list of security logs for the specified date
     */
    public List<EntryExitLog> getSecurityLogsByDate(Date date) {
        List<EntryExitLog> logs = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int targetDay = cal.get(Calendar.DAY_OF_MONTH);
        int targetMonth = cal.get(Calendar.MONTH);
        int targetYear = cal.get(Calendar.YEAR);

        for (List<EntryExitLog> logList : securityLogs.values()) {
            for (EntryExitLog log : logList) {
                cal.setTime(log.getEntryTime());
                if (cal.get(Calendar.DAY_OF_MONTH) == targetDay &&
                    cal.get(Calendar.MONTH) == targetMonth &&
                    cal.get(Calendar.YEAR) == targetYear) {
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    /**
     * Counts the number of available parking spots for a specific spot type.
     *
     * @param spotType The type of parking spot
     * @return The number of available spots of the specified type
     */
    public int getAvailableSpotsByType(String spotType) {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.getType().equals(spotType) && spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves the subscription manager for the parking lot.
     *
     * @return The subscription manager instance
     */
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    /**
     * Retrieves all payment transactions recorded in the parking lot.
     *
     * @return A list of payment transactions
     */
    public List<Payment> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Finds an available parking spot for a given vehicle type.
     *
     * @param vehicleType The type of vehicle
     * @return An available parking spot, or null if none is found
     */
    private ParkingSpot findAvailableSpot(String vehicleType) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.canFit(vehicleType)) {
                return spot;
            }
        }
        return null;
    }

    /**
     * Finds an available parking spot for a reservation for a given vehicle type and time period.
     *
     * @param vehicleType The type of vehicle
     * @param startTime   The start time of the reservation
     * @param endTime     The end time of the reservation
     * @return An available parking spot, or null if none is found
     */
    private ParkingSpot findAvailableSpotForReservation(String vehicleType, Date startTime, Date endTime) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailableForReservation(startTime, endTime) && spot.canFit(vehicleType)) {
                return spot;
            }
        }
        return null;
    }

    /**
     * Saves the parking lot data to a file.
     *
     * @param filePath The path to the file where data will be saved
     * @throws IOException If an I/O error occurs
     */
    public void saveDataToFile(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        }
    }

    /**
     * Loads parking lot data from a file.
     *
     * @param filePath The path to the file containing the data
     * @return The loaded ParkingLot instance
     * @throws IOException            If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    public static ParkingLot loadDataFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            ParkingLot lot = (ParkingLot) ois.readObject();
            lot.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return lot;
        }
    }

    /**
     * Logs a transaction with the specified details.
     *
     * @param transactionDetails The details of the transaction to log
     */
    @Override
    public void logTransaction(String transactionDetails) {
        System.out.println("Transaction Log: " + transactionDetails);
    }

    /**
     * Retrieves the name of the parking lot.
     *
     * @return The name of the parking lot
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of parking spots in the parking lot.
     *
     * @return The list of parking spots
     */
    public List<ParkingSpot> getSpots() {
        return spots;
    }

    /**
     * Retrieves the list of reservations in the parking lot.
     *
     * @return The list of reservations
     */
    public List<Reservation> getReservations() {
        return reservations;
    }
}