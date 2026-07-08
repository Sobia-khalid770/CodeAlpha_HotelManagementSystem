package com.hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A booking of a specific room, by a specific guest, for a date range.
 * Holds an embedded Payment once payment has been simulated successfully.
 */
public class Reservation {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String reservationId;
    private final int roomNumber;
    private Guest guest;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private ReservationStatus status;
    private final LocalDateTime bookedAt;
    private Payment payment; // nullable until paid

    public Reservation(String reservationId, int roomNumber, Guest guest,
                        LocalDate checkIn, LocalDate checkOut, double totalAmount,
                        ReservationStatus status, LocalDateTime bookedAt) {
        this.reservationId = reservationId;
        this.roomNumber = roomNumber;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public long getNights() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public String getReservationId() {
        return reservationId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public Guest getGuest() {
        return guest;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /** True if [checkIn, checkOut) overlaps the given date range. */
    public boolean overlaps(LocalDate otherIn, LocalDate otherOut) {
        return checkIn.isBefore(otherOut) && otherIn.isBefore(checkOut);
    }

    public String toCsv() {
        String paymentToken = (payment == null) ? "-" : payment.toCsv().replace(",", ";");
        return String.join(",",
                reservationId,
                String.valueOf(roomNumber),
                guest.toCsv(),
                checkIn.format(DATE_FORMAT),
                checkOut.format(DATE_FORMAT),
                String.valueOf(totalAmount),
                status.name(),
                bookedAt.format(TS_FORMAT),
                paymentToken);
    }

    public static Reservation fromCsv(String line) {
        // Fields are pipe/comma safe because Guest and Payment escape their own commas.
        String[] parts = splitTopLevel(line);
        Reservation r = new Reservation(
                parts[0],
                Integer.parseInt(parts[1]),
                Guest.fromCsv(parts[2]),
                LocalDate.parse(parts[3], DATE_FORMAT),
                LocalDate.parse(parts[4], DATE_FORMAT),
                Double.parseDouble(parts[5]),
                ReservationStatus.valueOf(parts[6]),
                LocalDateTime.parse(parts[7], TS_FORMAT));
        if (parts.length > 8 && !parts[8].equals("-")) {
            r.setPayment(Payment.fromCsv(parts[8].replace(";", ",")));
        }
        return r;
    }

    // Guest token uses '|' internally so a naive split(",") on the whole line
    // works, EXCEPT the guest token itself may legitimately contain no commas
    // (they're substituted) so a straightforward comma split remains safe.
    private static String[] splitTopLevel(String line) {
        return line.split(",", -1);
    }

    @Override
    public String toString() {
        return String.format("[%s] Room %d | %s -> %s | %s | $%.2f",
                reservationId, roomNumber, checkIn, checkOut, status, totalAmount);
    }
}
