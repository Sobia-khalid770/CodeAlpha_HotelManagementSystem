package com.hotel.service;

import com.hotel.model.*;
import com.hotel.storage.FileStorageManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central business-logic facade. Owns the in-memory catalogue of rooms and
 * reservations, enforces booking rules (no double-booking, valid date
 * ranges, etc.), and delegates persistence to {@link FileStorageManager}.
 *
 * This is the single class the UI layer talks to — it never touches the
 * storage layer or model internals directly, which keeps concerns cleanly
 * separated (a small in-process take on a service/repository pattern).
 */
public class HotelService {

    private final FileStorageManager storage;
    private final List<Room> rooms;
    private final List<Reservation> reservations;

    public HotelService(FileStorageManager storage) {
        this.storage = storage;
        this.rooms = new ArrayList<>(storage.loadRooms());
        this.reservations = new ArrayList<>(storage.loadReservations());
        if (rooms.isEmpty()) {
            seedDefaultRooms();
            persistRooms();
        }
    }

    // ---------------------------------------------------------------
    // Room inventory
    // ---------------------------------------------------------------

    private void seedDefaultRooms() {
        int roomNumber = 101;
        for (int floor = 1; floor <= 3; floor++) {
            for (int i = 0; i < 4; i++) {
                RoomType type;
                if (i < 2) type = RoomType.STANDARD;
                else if (i == 2) type = RoomType.DELUXE;
                else type = RoomType.SUITE;
                rooms.add(new Room(roomNumber, type, floor, type.getBaseRate()));
                roomNumber++;
            }
            roomNumber = (floor + 1) * 100 + 1;
        }
    }

    public List<Room> getAllRooms() {
        return rooms.stream().filter(Room::isActive)
                .sorted(Comparator.comparingInt(Room::getRoomNumber))
                .collect(Collectors.toList());
    }

    public Optional<Room> findRoom(int roomNumber) {
        return rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst();
    }

    public Room addRoom(int roomNumber, RoomType type, int floor, double price) {
        if (findRoom(roomNumber).isPresent()) {
            throw new IllegalArgumentException("Room " + roomNumber + " already exists.");
        }
        Room room = new Room(roomNumber, type, floor, price);
        rooms.add(room);
        persistRooms();
        return room;
    }

    public void removeRoom(int roomNumber) {
        Room room = findRoom(roomNumber)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomNumber));
        room.setActive(false);
        persistRooms();
    }

    /**
     * Rooms of the given type (or any type if null) that have no CONFIRMED
     * reservation overlapping the requested date range.
     */
    public List<Room> searchAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        validateDateRange(checkIn, checkOut);
        Set<Integer> bookedRoomNumbers = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> r.overlaps(checkIn, checkOut))
                .map(Reservation::getRoomNumber)
                .collect(Collectors.toSet());

        return rooms.stream()
                .filter(Room::isActive)
                .filter(r -> type == null || r.getType() == type)
                .filter(r -> !bookedRoomNumbers.contains(r.getRoomNumber()))
                .sorted(Comparator.comparingInt(Room::getRoomNumber))
                .collect(Collectors.toList());
    }

    private void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
    }

    // ---------------------------------------------------------------
    // Reservations
    // ---------------------------------------------------------------

    public List<Reservation> getAllReservations() {
        return reservations.stream()
                .sorted(Comparator.comparing(Reservation::getBookedAt).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Reservation> findReservation(String id) {
        return reservations.stream().filter(r -> r.getReservationId().equals(id)).findFirst();
    }

    /**
     * Books a room, re-validating availability at booking time to guard
     * against races between search and confirm within the same session.
     */
    public Reservation bookRoom(int roomNumber, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        validateDateRange(checkIn, checkOut);
        Room room = findRoom(roomNumber)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomNumber));
        if (!room.isActive()) {
            throw new IllegalStateException("Room " + roomNumber + " is not available for booking.");
        }
        boolean conflict = reservations.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .anyMatch(r -> r.overlaps(checkIn, checkOut));
        if (conflict) {
            throw new IllegalStateException("Room " + roomNumber + " is already booked for those dates.");
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * room.getPricePerNight();

        Reservation reservation = new Reservation(
                generateReservationId(),
                roomNumber,
                guest,
                checkIn,
                checkOut,
                total,
                ReservationStatus.CONFIRMED,
                LocalDateTime.now());
        reservations.add(reservation);
        persistReservations();
        return reservation;
    }

    public void attachPayment(String reservationId, Payment payment) {
        Reservation reservation = findReservation(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found: " + reservationId));
        reservation.setPayment(payment);
        persistReservations();
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = findReservation(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found: " + reservationId));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        if (reservation.getPayment() != null && reservation.getPayment().getStatus() == PaymentStatus.SUCCESS) {
            Payment refund = new Payment(
                    "RF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    reservation.getPayment().getAmount(),
                    reservation.getPayment().getMethod(),
                    reservation.getPayment().getMaskedReference(),
                    PaymentStatus.REFUNDED,
                    LocalDateTime.now());
            reservation.setPayment(refund);
        }
        persistReservations();
    }

    private String generateReservationId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ---------------------------------------------------------------
    // Persistence helpers
    // ---------------------------------------------------------------

    private void persistRooms() {
        storage.saveRooms(rooms);
    }

    private void persistReservations() {
        storage.saveReservations(reservations);
    }
}
