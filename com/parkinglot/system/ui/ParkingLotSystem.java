package com.parkinglot.system.ui;

import com.parkinglot.system.exceptions.InvalidReservationException;
import com.parkinglot.system.exceptions.NoAvailableSpotException;
import com.parkinglot.system.exceptions.VehicleNotFoundException;
import com.parkinglot.system.management.ParkingLot;
import com.parkinglot.system.model.*;
import com.parkinglot.system.payment.*;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * A command-line interface for managing a parking lot system.
 * Provides options to park vehicles, make reservations, manage subscriptions, view logs, and more.
 * Implements Serializable to save and load the parking lot state.
 */
public class ParkingLotSystem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "parking_lot_data.dat";

    /**
     * Main entry point for the parking lot system, initializing and running the command-line interface.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            ParkingLot parkingLot = initializeParkingLot();
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            boolean running = true;
            while (running) {
                displayMainMenu();
                int option = getIntInput(scanner, "Choose an option: ", 1, 11);

                switch (option) {
                    case 1 -> parkVehicle(parkingLot, scanner);
                    case 2 -> makeReservation(parkingLot, scanner, dateFormat);
                    case 3 -> useReservation(parkingLot, scanner, dateFormat);
                    case 4 -> cancelReservation(parkingLot, scanner);
                    case 5 -> checkActiveReservations(parkingLot, dateFormat);
                    case 6 -> vehicleExit(parkingLot, scanner);
                    case 7 -> checkAvailability(parkingLot);
                    case 8 -> manageSubscriptions(parkingLot, scanner);
                    case 9 -> viewSecurityLogs(parkingLot, scanner, dateFormat);
                    case 10 -> viewAllTransactions(parkingLot, dateFormat);
                    case 11 -> {
                        saveAndExit(parkingLot);
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
            scanner.close();

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the parking lot by loading existing data or creating a new instance.
     *
     * @return The initialized ParkingLot instance
     */
    private static ParkingLot initializeParkingLot() {
        File file = new File(DATA_FILE);
        ParkingLot parkingLot;

        if (file.exists()) {
            try {
                parkingLot = ParkingLot.loadDataFromFile(DATA_FILE);
                System.out.println("Parking lot data loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not load parking lot data: " + quote(e.getMessage()));
                parkingLot = new ParkingLot("City Center Parking", 100);
            }
        } else {
            parkingLot = new ParkingLot("City Center Parking", 100);
        }
        return parkingLot;
    }

    /**
     * Displays the main menu options for the parking lot system.
     */
    private static void displayMainMenu() {
        System.out.println("\n===== PARKING LOT MANAGEMENT SYSTEM =====");
        System.out.println("1. Park a vehicle");
        System.out.println("2. Make a reservation");
        System.out.println("3. Use existing reservation");
        System.out.println("4. Cancel reservation");
        System.out.println("5. Check active reservations");
        System.out.println("6. Vehicle exit and payment");
        System.out.println("7. Check parking availability");
        System.out.println("8. Subscription management");
        System.out.println("9. View security logs");
        System.out.println("10. View all transactions");
        System.out.println("11. Exit program");
    }

    /**
     * Saves the parking lot data and exits the program.
     *
     * @param parkingLot The ParkingLot instance to save
     */
    private static void saveAndExit(ParkingLot parkingLot) {
        try {
            parkingLot.saveDataToFile(DATA_FILE);
            System.out.println("All data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + quote(e.getMessage()));
        }
        System.out.println("Thank you for using the Parking Lot Management System!");
    }

    /**
     * Handles the process of parking a vehicle in the parking lot.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void parkVehicle(ParkingLot parkingLot, Scanner scanner) {
        try {
            System.out.println("\n-- Park a Vehicle --");
            String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

            Vehicle vehicle = createVehicle(scanner, licensePlate);
            if (vehicle == null) return;

            if (parkingLot.isVehicleParked(licensePlate)) {
                System.out.println("Error: A vehicle with this license plate is already parked.");
                return;
            }

            ParkingSpot spot = parkingLot.checkIn(vehicle);
            System.out.println("Vehicle parked successfully!");
            System.out.println("Spot assigned: " + spot.getSpotId());

        } catch (NoAvailableSpotException e) {
            System.out.println("Error: " + quote(e.getMessage()));
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + quote(e.getMessage()));
        }
    }

    /**
     * Creates a vehicle instance based on user input.
     *
     * @param scanner      The Scanner for user input
     * @param licensePlate The license plate of the vehicle
     * @return The created Vehicle instance, or null if invalid
     */
    private static Vehicle createVehicle(Scanner scanner, String licensePlate) {
        System.out.println("Vehicle type:");
        System.out.println("1. Car");
        System.out.println("2. Bike");
        System.out.println("3. Truck");
        System.out.println("4. Electric Vehicle");

        int vehicleType = getIntInput(scanner, "Choose vehicle type: ", 1, 4);
        return switch (vehicleType) {
            case 1 -> {
                boolean isLuxury = getStringInput(scanner, "Is it a luxury car? (y/n): ").equalsIgnoreCase("y");
                yield new Car(licensePlate, isLuxury);
            }
            case 2 -> new Bike(licensePlate);
            case 3 -> {
                double weight = getDoubleInput(scanner, "Enter truck weight in tons: ", 0.1, 50.0);
                yield new Truck(licensePlate, weight);
            }
            case 4 -> {
                double batteryCapacity = getDoubleInput(scanner, "Enter battery capacity in kWh: ", 1.0, 200.0);
                yield new ElectricVehicle(licensePlate, batteryCapacity);
            }
            default -> {
                System.out.println("Invalid vehicle type.");
                yield null;
            }
        };
    }

    /**
     * Handles the process of making a parking reservation.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     * @param dateFormat The date format for parsing input
     */
    private static void makeReservation(ParkingLot parkingLot, Scanner scanner, SimpleDateFormat dateFormat) {
        try {
            System.out.println("\n-- Make a Reservation --");
            String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

            System.out.println("Vehicle type:");
            System.out.println("1. Car");
            System.out.println("2. Bike");
            System.out.println("3. Truck");

            int vehicleType = getIntInput(scanner, "Choose vehicle type: ", 1, 3);
            Vehicle vehicle = switch (vehicleType) {
                case 1 -> new Car(licensePlate, false);
                case 2 -> new Bike(licensePlate);
                case 3 -> new Truck(licensePlate, 1.0);
                default -> {
                    System.out.println("Invalid vehicle type.");
                    yield null;
                }
            };
            if (vehicle == null) return;

            Date startTime = getDateInput(scanner, "Enter start date and time (dd-MM-yyyy HH:mm): ", dateFormat);
            Date endTime = getDateInput(scanner, "Enter end date and time (dd-MM-yyyy HH:mm): ", dateFormat);

            if (parkingLot.hasReservationConflict(vehicle, startTime, endTime)) {
                System.out.println("Error: This vehicle already has a reservation or is parked during this time.");
                return;
            }

            Reservation reservation = parkingLot.makeReservation(vehicle, startTime, endTime);
            System.out.println("Reservation made successfully!");
            System.out.println("Spot assigned: " + reservation.getSpot().getSpotId());
            System.out.println("Start time: " + dateFormat.format(startTime));
            System.out.println("End time: " + dateFormat.format(endTime));

        } catch (NoAvailableSpotException | InvalidReservationException e) {
            System.out.println("Error: " + quote(e.getMessage()));
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + quote(e.getMessage()));
        }
    }

    /**
     * Handles checking in a vehicle using an existing reservation.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     * @param dateFormat The date format for displaying times
     */
    private static void useReservation(ParkingLot parkingLot, Scanner scanner, SimpleDateFormat dateFormat) {
        try {
            System.out.println("\n-- Use Existing Reservation --");
            String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

            Reservation reservation = parkingLot.findReservationByLicensePlate(licensePlate);
            if (reservation == null) {
                System.out.println("No active reservation found for this vehicle.");
                return;
            }

            Date now = new Date();
            long tenMinutesInMillis = 10 * 60 * 1000;
            Date adjustedStartTime = new Date(reservation.getStartTime().getTime() - tenMinutesInMillis);
            Date adjustedEndTime = new Date(reservation.getEndTime().getTime() + tenMinutesInMillis);

            if (now.before(adjustedStartTime) || now.after(adjustedEndTime)) {
                System.out.println("Current time is outside the reservation period (±10 minutes).");
                return;
            }

            ParkingSpot spot = parkingLot.checkIn(reservation.getVehicle(), reservation);
            reservation.setStatus(Reservation.STATUS_CHECKED_IN);

            System.out.println("Vehicle checked in successfully using reservation!");
            System.out.println("Spot assigned: " + spot.getSpotId());
            System.out.println("Entry time: " + dateFormat.format(now));

        } catch (Exception e) {
            System.out.println("Error: " + quote(e.getMessage()));
        }
    }

    /**
     * Handles canceling a reservation for a vehicle.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void cancelReservation(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n-- Cancel Reservation --");
        String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

        boolean success = parkingLot.cancelReservationByLicensePlate(licensePlate);
        System.out.println(success ? "Reservation cancelled successfully."
                                  : "No active reservation found for this vehicle.");
    }

    /**
     * Displays all active reservations in the parking lot.
     *
     * @param parkingLot The ParkingLot instance
     * @param dateFormat The date format for displaying times
     */
    private static void checkActiveReservations(ParkingLot parkingLot, SimpleDateFormat dateFormat) {
        System.out.println("\n-- Active Reservations --");
        List<Reservation> activeReservations = parkingLot.getAllActiveReservations();

        if (activeReservations.isEmpty()) {
            System.out.println("No active reservations found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-15s %-12s %-20s %-20s %-12s\n",
                "License Plate", "Vehicle Type", "Spot ID", "Start Time", "End Time", "Status");
        System.out.println("------------------------------------------------------------------------------------------");

        for (Reservation res : activeReservations) {
            System.out.printf("%-15s %-15s %-12s %-20s %-20s %-12s\n",
                    res.getVehicle().getLicensePlate(),
                    res.getVehicle().getType(),
                    res.getSpot().getSpotId(),
                    dateFormat.format(res.getStartTime()),
                    dateFormat.format(res.getEndTime()),
                    res.getStatus());
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    /**
     * Handles the vehicle checkout and payment process.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void vehicleExit(ParkingLot parkingLot, Scanner scanner) {
        try {
            System.out.println("\n-- Vehicle Exit and Payment --");
            String licensePlate = getStringInput(scanner, "Enter license plate of exiting vehicle: ").toUpperCase();

            Vehicle vehicle = new Car(licensePlate); // Temporary vehicle for checkout
            Payment payment = parkingLot.prepareCheckOut(vehicle);
            if (payment.getAmount() == 0.0)
                return;
            System.out.println("Vehicle checkout prepared!");
            System.out.println("Duration: " + payment.getParkingSession().getParkingDuration() + " hours");
            System.out.println("Amount due: Rs. " + payment.getAmount());
            
            PaymentMethod method = selectPaymentMethod(scanner);
            if (method == null) return;

            boolean paymentSuccess = payment.processPayment(method);
            parkingLot.finalizeCheckOut(vehicle, paymentSuccess);

            System.out.println(paymentSuccess ? "Payment successful! Thank you for using our parking services."
                                             : "Payment failed. Please try again. Vehicle is still parked.");

        } catch (VehicleNotFoundException e) {
            System.out.println("Error: " + quote(e.getMessage()));
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + quote(e.getMessage()));
        }
    }

    /**
     * Prompts the user to select a payment method.
     *
     * @param scanner The Scanner for user input
     * @return The selected PaymentMethod, or null if invalid
     */
    private static PaymentMethod selectPaymentMethod(Scanner scanner) {
        System.out.println("\nPayment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. UPI");

        int paymentMethod = getIntInput(scanner, "Choose payment method: ", 1, 2);
        return switch (paymentMethod) {
            case 1 -> {
                String cardNumber = getStringInput(scanner, "Enter card number: ");
                String expiryDate = getStringInput(scanner, "Enter expiry date (MM/YY): ");
                String cvv = getStringInput(scanner, "Enter CVV: ");
                String cardHolderName = getStringInput(scanner, "Enter card holder name: ");
                yield new CreditCardPayment(cardNumber, expiryDate, cvv, cardHolderName);
            }
            case 2 -> {
                String upiId = getStringInput(scanner, "Enter UPI ID: ");
                yield new UPIPayment(upiId);
            }
            default -> {
                System.out.println("Invalid payment method.");
                yield null;
            }
        };
    }

    /**
     * Displays the availability of parking spots by type.
     *
     * @param parkingLot The ParkingLot instance
     */
    private static void checkAvailability(ParkingLot parkingLot) {
        System.out.println("\n-- Parking Availability --");
        System.out.println("Available spots by type:");
        System.out.println("Car spots: " + parkingLot.getAvailableSpotsByType(ParkingSpot.TYPE_CAR));
        System.out.println("Bike spots: " + parkingLot.getAvailableSpotsByType(ParkingSpot.TYPE_BIKE));
        System.out.println("Truck spots: " + parkingLot.getAvailableSpotsByType(ParkingSpot.TYPE_TRUCK));
        System.out.println("Electric spots: " + parkingLot.getAvailableSpotsByType(ParkingSpot.TYPE_ELECTRIC));
        System.out.println("Handicapped spots: " + parkingLot.getAvailableSpotsByType(ParkingSpot.TYPE_HANDICAPPED));
    }

    /**
     * Manages subscription-related operations.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void manageSubscriptions(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n-- Subscription Management --");
        System.out.println("1. Register new subscription");
        System.out.println("2. Check subscription status");
        System.out.println("3. Renew subscription");
        System.out.println("4. Cancel subscription");
        System.out.println("5. View all active subscriptions");

        int option = getIntInput(scanner, "Choose an option: ", 1, 5);
        switch (option) {
            case 1 -> registerNewSubscription(parkingLot, scanner);
            case 2 -> checkSubscriptionStatus(parkingLot, scanner);
            case 3 -> renewSubscription(parkingLot, scanner);
            case 4 -> cancelSubscription(parkingLot, scanner);
            case 5 -> viewActiveSubscriptions(parkingLot);
            default -> System.out.println("Invalid option.");
        }
    }

    /**
     * Registers a new subscription for a vehicle.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void registerNewSubscription(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n-- Register New Subscription --");
        String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

        if (parkingLot.getSubscriptionManager().hasActiveSubscription(licensePlate)) {
            System.out.println("This vehicle already has an active subscription.");
            return;
        }

        Vehicle vehicle = createVehicle(scanner, licensePlate);
        if (vehicle == null) return;

        String spotType = selectSpotType(vehicle);
        String subType = selectSubscriptionType(scanner);
        double fee = parkingLot.getSubscriptionManager().calculateSubscriptionFee(vehicle, subType, spotType);

        System.out.println("\nSubscription fee: Rs. " + fee);
        PaymentMethod method = selectPaymentMethod(scanner);
        if (method == null) return;

        if (confirmAction(scanner, "Confirm subscription? (y/n): ")) {
            System.out.println("Processing payment...");
            boolean paymentSuccess = parkingLot.getSubscriptionManager().processSubscriptionPayment(
                    vehicle, subType, spotType, method);

            if (paymentSuccess) {
                Subscription subscription = parkingLot.getSubscriptionManager().getSubscription(licensePlate);
                System.out.println("Subscription registered successfully!");
                System.out.println("Subscription ID: " + subscription.getSubscriptionId());
                System.out.println("Valid until: " + subscription.getEndDate());
                saveData(parkingLot);
            } else {
                System.out.println("Payment failed. Subscription not registered.");
            }
        } else {
            System.out.println("Subscription registration cancelled.");
        }
    }

    /**
     * Selects the appropriate parking spot type for a vehicle.
     *
     * @param vehicle The vehicle to assign a spot type
     * @return The selected parking spot type
     */
    private static String selectSpotType(Vehicle vehicle) {
        if (vehicle instanceof Car) {
            return ParkingSpot.TYPE_CAR;
        } else if (vehicle instanceof Bike) {
            return ParkingSpot.TYPE_BIKE;
        } else if (vehicle instanceof Truck) {
            return ParkingSpot.TYPE_TRUCK;
        } else if (vehicle instanceof ElectricVehicle) {
            return ParkingSpot.TYPE_ELECTRIC;
        } else {
            return ParkingSpot.TYPE_CAR; // Default fallback
        }
    }

    /**
     * Prompts the user to select a subscription type.
     *
     * @param scanner The Scanner for user input
     * @return The selected subscription type
     */
    private static String selectSubscriptionType(Scanner scanner) {
        System.out.println("\nSubscription type:");
        System.out.println("1. Monthly");
        System.out.println("2. Quarterly (3 months - 10% discount)");
        System.out.println("3. Semi-Annual (6 months - 15% discount)");
        System.out.println("4. Annual (12 months - 20% discount)");

        int subTypeChoice = getIntInput(scanner, "Choose subscription type: ", 1, 4);
        return switch (subTypeChoice) {
            case 1 -> Subscription.TYPE_MONTHLY;
            case 2 -> Subscription.TYPE_QUARTERLY;
            case 3 -> Subscription.TYPE_SEMI_ANNUAL;
            case 4 -> Subscription.TYPE_ANNUAL;
            default -> Subscription.TYPE_MONTHLY;
        };
    }

    /**
     * Checks the status of a subscription for a vehicle.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void checkSubscriptionStatus(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n-- Check Subscription Status --");
        String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

        Subscription subscription = parkingLot.getSubscriptionManager().getSubscription(licensePlate);
        if (subscription == null) {
            System.out.println("No subscription found for this vehicle.");
        } else {
            System.out.println("\nSubscription details:");
            System.out.println("ID: " + subscription.getSubscriptionId());
            System.out.println("Type: " + subscription.getType());
            System.out.println("Start date: " + subscription.getStartDate());
            System.out.println("End date: " + subscription.getEndDate());
            System.out.println("Status: " + (subscription.isActive() ? "Active" : "Inactive"));
            System.out.println("Parking spot type: " + subscription.getSpotType());
        }
    }

    /**
     * Handles renewing an existing subscription.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void renewSubscription(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n-- Renew Subscription --");
        String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

        Subscription subscription = parkingLot.getSubscriptionManager().getSubscription(licensePlate);
        if (subscription == null) {
            System.out.println("No subscription found for this vehicle.");
            return;
        }

        System.out.println("\nCurrent subscription ends on: " + subscription.getEndDate());
        System.out.println("\nRenewal period:");
        System.out.println("1. 1 month");
        System.out.println("2. 3 months");
        System.out.println("3. 6 months");
        System.out.println("4. 12 months");

        int periodChoice = getIntInput(scanner, "Choose renewal period: ", 1, 4);
        var renewal = selectRenewalPeriod(periodChoice);
        int months = renewal.months();
        String subType = renewal.subType();

        double fee = parkingLot.getSubscriptionManager().calculateSubscriptionFee(
                subscription.getVehicle(), subType, subscription.getSpotType());
        System.out.println("\nRenewal fee: Rs. " + fee);

        PaymentMethod method = selectPaymentMethod(scanner);
        if (method == null) return;

        if (confirmAction(scanner, "Confirm renewal? (y/n): ")) {
            System.out.println("Processing payment...");
            boolean paymentSuccess = parkingLot.getSubscriptionManager().processSubscriptionPayment(
                    subscription.getVehicle(), subType, subscription.getSpotType(), method);

            if (paymentSuccess) {
                parkingLot.getSubscriptionManager().renewSubscription(licensePlate, months);
                System.out.println("Subscription renewed successfully!");
                System.out.println("New end date: " + subscription.getEndDate());
                saveData(parkingLot);
            } else {
                System.out.println("Payment failed. Subscription not renewed.");
            }
        } else {
            System.out.println("Renewal cancelled.");
        }
    }

    /**
     * Represents a renewal period with the number of months and subscription type.
     *
     * @param months   The number of months for renewal
     * @param subType  The subscription type for renewal
     */
    private record RenewalPeriod(int months, String subType) {
    }

    /**
     * Selects the renewal period based on user choice.
     *
     * @param periodChoice The user's choice for renewal period
     * @return The selected RenewalPeriod
     */
    private static RenewalPeriod selectRenewalPeriod(int periodChoice) {
        return switch (periodChoice) {
            case 1 -> new RenewalPeriod(1, Subscription.TYPE_MONTHLY);
            case 2 -> new RenewalPeriod(3, Subscription.TYPE_QUARTERLY);
            case 3 -> new RenewalPeriod(6, Subscription.TYPE_SEMI_ANNUAL);
            case 4 -> new RenewalPeriod(12, Subscription.TYPE_ANNUAL);
            default -> new RenewalPeriod(1, Subscription.TYPE_MONTHLY);
        };
    }

    /**
     * Handles canceling an existing subscription.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     */
    private static void cancelSubscription(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n-- Cancel Subscription --");
        String licensePlate = getStringInput(scanner, "Enter license plate: ").toUpperCase();

        Subscription subscription = parkingLot.getSubscriptionManager().getSubscription(licensePlate);
        if (subscription == null) {
            System.out.println("No subscription found for this vehicle.");
            return;
        }
        if (!subscription.isActive()) {
            System.out.println("This subscription is already inactive.");
            return;
        }

        System.out.println("\nSubscription details:");
        System.out.println("ID: " + subscription.getSubscriptionId());
        System.out.println("Type: " + subscription.getType());
        System.out.println("End date: " + subscription.getEndDate());

        if (confirmAction(scanner, "Are you sure you want to cancel this subscription? (y/n): ")) {
            parkingLot.getSubscriptionManager().cancelSubscription(licensePlate);
            System.out.println("Subscription cancelled successfully.");
            saveData(parkingLot);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    /**
     * Displays all active subscriptions in the parking lot.
     *
     * @param parkingLot The ParkingLot instance
     */
    private static void viewActiveSubscriptions(ParkingLot parkingLot) {
        System.out.println("\n-- Active Subscriptions --");
        List<Subscription> activeSubscriptions = parkingLot.getSubscriptionManager().getAllActiveSubscriptions();

        if (activeSubscriptions.isEmpty()) {
            System.out.println("No active subscriptions found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-12s %-12s %-15s\n",
                "ID", "License Plate", "Type", "Spot Type", "End Date");
        System.out.println("------------------------------------------------------------------------------------------");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        for (Subscription sub : activeSubscriptions) {
            System.out.printf("%-10s %-15s %-12s %-12s %-15s\n",
                    sub.getSubscriptionId().substring(0, 8),
                    sub.getVehicle().getLicensePlate(),
                    sub.getType(),
                    sub.getSpotType(),
                    dateFormat.format(sub.getEndDate()));
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    /**
     * Displays all payment transactions in the parking lot.
     *
     * @param parkingLot The ParkingLot instance
     * @param dateFormat The date format for displaying times
     */
    private static void viewAllTransactions(ParkingLot parkingLot, SimpleDateFormat dateFormat) {
        System.out.println("\n-- All Transactions --");
        List<Payment> transactions = parkingLot.getAllTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-10s %-20s %-12s\n",
                "License Plate", "Amount", "Status", "Payment Time", "Method");
        System.out.println("------------------------------------------------------------------------------------------");

        for (Payment payment : transactions) {
            String method = payment.getMethod() != null ? payment.getMethod().getPaymentDetails() : "N/A";
            System.out.printf("%-15s Rs. %-9.2f %-10s %-20s %-12s\n",
                    payment.getVehicle().getLicensePlate(),
                    payment.getAmount(),
                    payment.getStatus(),
                    dateFormat.format(payment.getPaymentTime()),
                    method);
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    /**
     * Handles viewing security logs, either all logs or logs for a specific date.
     *
     * @param parkingLot The ParkingLot instance
     * @param scanner    The Scanner for user input
     * @param dateFormat The date format for displaying times
     */
    private static void viewSecurityLogs(ParkingLot parkingLot, Scanner scanner, SimpleDateFormat dateFormat) {
        System.out.println("\n-- Security Logs --");
        System.out.println("1. View all logs");
        System.out.println("2. View logs by date");

        int option = getIntInput(scanner, "Choose an option: ", 1, 2);
        switch (option) {
            case 1 -> displayAllLogs(parkingLot, dateFormat);
            case 2 -> {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = getDateInput(scanner, "Enter date (dd-MM-yyyy): ", inputFormat);
                    displayLogsByDate(parkingLot, date, dateFormat);
                } catch (Exception e) {
                    System.out.println("Error: " + quote(e.getMessage()));
                }
            }
            default -> System.out.println("Invalid option.");
        }
    }

    /**
     * Displays all security logs in the parking lot.
     *
     * @param parkingLot The ParkingLot instance
     * @param dateFormat The date format for displaying times
     */
    private static void displayAllLogs(ParkingLot parkingLot, SimpleDateFormat dateFormat) {
        Map<String, List<EntryExitLog>> logs = parkingLot.getSecurityLogs();
        if (logs.isEmpty()) {
            System.out.println("No security logs found.");
            return;
        }

        System.out.println("\n-- All Security Logs --");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-20s %-20s %-12s\n",
                "License Plate", "Spot ID", "Entry Time", "Exit Time", "Vehicle Type");
        System.out.println("------------------------------------------------------------------------------------------");

        for (Map.Entry<String, List<EntryExitLog>> entry : logs.entrySet()) {
            String licensePlate = entry.getKey();
            for (EntryExitLog log : entry.getValue()) {
                String exitTime = log.getExitTime() != null ? dateFormat.format(log.getExitTime()) : "Still Parked";
                System.out.printf("%-15s %-12s %-20s %-20s %-12s\n",
                        licensePlate,
                        log.getSpot().getSpotId(),
                        dateFormat.format(log.getEntryTime()),
                        exitTime,
                        log.getVehicle().getType());
            }
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    /**
     * Displays security logs for a specific date.
     *
     * @param parkingLot The ParkingLot instance
     * @param date       The date to filter logs
     * @param dateFormat The date format for displaying times
     */
    private static void displayLogsByDate(ParkingLot parkingLot, Date date, SimpleDateFormat dateFormat) {
        List<EntryExitLog> logs = parkingLot.getSecurityLogsByDate(date);
        if (logs.isEmpty()) {
            System.out.println("No logs found for the specified date.");
            return;
        }

        System.out.println("\nSecurity Logs for " + new SimpleDateFormat("dd-MM-yyyy").format(date) + ":");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-20s %-20s\n", "License Plate", "Spot ID", "Entry Time", "Exit Time");
        System.out.println("------------------------------------------------------------");

        for (EntryExitLog log : logs) {
            String exitTime = log.getExitTime() != null ? dateFormat.format(log.getExitTime()) : "Still Parked";
            System.out.printf("%-15s %-12s %-20s %-20s\n",
                    log.getVehicle().getLicensePlate(),
                    log.getSpot().getSpotId(),
                    dateFormat.format(log.getEntryTime()),
                    exitTime);
        }
        System.out.println("------------------------------------------------------------");
    }

    /**
     * Saves the parking lot data to a file.
     *
     * @param parkingLot The ParkingLot instance
     */
    private static void saveData(ParkingLot parkingLot) {
        try {
            parkingLot.saveDataToFile(DATA_FILE);
            System.out.println("All data saved.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + quote(e.getMessage()));
        }
    }

    /**
     * Prompts the user to confirm an action.
     *
     * @param scanner The Scanner for user input
     * @param prompt  The confirmation prompt
     * @return True if the user confirms, false otherwise
     */
    private static boolean confirmAction(Scanner scanner, String prompt) {
        return getStringInput(scanner, prompt).equalsIgnoreCase("y");
    }

    /**
     * Retrieves an integer input from the user within a specified range.
     *
     * @param scanner The Scanner for user input
     * @param prompt  The input prompt
     * @param min     The minimum allowed value
     * @param max     The maximum allowed value
     * @return The validated integer input
     */
    private static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine();
            }
        }
    }

    /**
     * Retrieves a double input from the user within a specified range.
     *
     * @param scanner The Scanner for user input
     * @param prompt  The input prompt
     * @param min     The minimum allowed value
     * @param max     The maximum allowed value
     * @return The validated double input
     */
    private static double getDoubleInput(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                scanner.nextLine();
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine();
            }
        }
    }

    /**
     * Retrieves a non-empty string input from the user.
     *
     * @param scanner The Scanner for user input
     * @param prompt  The input prompt
     * @return The validated string input
     */
    private static String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print("Input cannot be empty. " + prompt);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    /**
     * Retrieves a date input from the user in the specified format.
     *
     * @param scanner    The Scanner for user input
     * @param prompt     The input prompt
     * @param dateFormat The date format for parsing
     * @return The parsed Date object
     */
    private static Date getDateInput(Scanner scanner, String prompt, SimpleDateFormat dateFormat) {
        while (true) {
            System.out.print(prompt);
            try {
                return dateFormat.parse(scanner.nextLine().trim());
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use " + dateFormat.toPattern());
            }
        }
    }

    /**
     * Wraps a string in quotation marks for error messages.
     *
     * @param str The string to quote
     * @return The quoted string
     */
    private static String quote(String str) {
        return "\"" + str + "\"";
    }
}