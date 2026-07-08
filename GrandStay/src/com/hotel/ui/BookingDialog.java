package com.hotel.ui;

import com.hotel.model.Guest;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.HotelService;
import com.hotel.service.PaymentSimulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modal dialog that collects guest details, shows a price breakdown, and
 * then hands off to {@link PaymentDialog} to simulate payment before the
 * reservation is actually persisted via HotelService.
 */
public class BookingDialog extends JDialog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");

    private final HotelService hotelService;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private boolean bookingCompleted = false;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    public BookingDialog(Frame owner, HotelService hotelService, Room room, LocalDate checkIn, LocalDate checkOut) {
        super(owner, "Book " + room, true);
        this.hotelService = hotelService;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;

        setSize(480, 600);
        setMinimumSize(new Dimension(420, 420));
        setLocationRelativeTo(owner);
        setResizable(true);
        buildUI();
    }

    public boolean wasBookingCompleted() {
        return bookingCompleted;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(20, 24, 20, 24));
        root.setBackground(UITheme.BG);
        setContentPane(root);

        JLabel title = UITheme.sectionTitle("Guest Details");
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);
        gc.gridx = 0;
        gc.weightx = 1;

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * room.getPricePerNight();

        JPanel summary = UITheme.card(new GridLayout(0, 1, 0, 4));
        summary.add(boldRow("Room", room.toString()));
        summary.add(boldRow("Type", room.getType().getDisplayName()));
        summary.add(boldRow("Check-In", checkIn.format(FMT)));
        summary.add(boldRow("Check-Out", checkOut.format(FMT)));
        summary.add(boldRow("Nights", String.valueOf(nights)));
        summary.add(boldRow("Rate / Night", String.format("$%.2f", room.getPricePerNight())));
        JLabel totalLabel = boldRow("Total Amount", String.format("$%.2f", total));
        totalLabel.setForeground(UITheme.SUCCESS);
        summary.add(totalLabel);

        gc.gridy = 0;
        form.add(summary, gc);

        gc.gridy = 1;
        form.add(Box.createVerticalStrut(8), gc);

        gc.gridy = 2;
        form.add(labeled("Full Name *"), gc);
        gc.gridy = 3;
        nameField = new JTextField();
        UITheme.styleTextField(nameField);
        form.add(nameField, gc);

        gc.gridy = 4;
        form.add(labeled("Email *"), gc);
        gc.gridy = 5;
        emailField = new JTextField();
        UITheme.styleTextField(emailField);
        form.add(emailField, gc);

        gc.gridy = 6;
        form.add(labeled("Phone *"), gc);
        gc.gridy = 7;
        phoneField = new JTextField();
        UITheme.styleTextField(phoneField);
        form.add(phoneField, gc);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(UITheme.BG);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(formScroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(UITheme.BG);
        JButton cancel = UITheme.secondaryButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton proceed = UITheme.goldButton("Proceed to Payment");
        proceed.addActionListener(e -> handleProceed(total));
        buttons.add(cancel);
        buttons.add(proceed);
        root.add(buttons, BorderLayout.SOUTH);
    }

    private void handleProceed(double total) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                    "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentDialog paymentDialog = new PaymentDialog((Frame) getOwner(), total);
        paymentDialog.setVisible(true);
        if (!paymentDialog.wasConfirmed()) {
            return; // user cancelled payment; stay on booking dialog
        }

        try {
            Guest guest = new Guest(name, email, phone);
            Reservation reservation = hotelService.bookRoom(room.getRoomNumber(), guest, checkIn, checkOut);

            PaymentSimulator simulator = new PaymentSimulator();
            Payment payment = simulator.process(total, paymentDialog.getSelectedMethod(), paymentDialog.getReferenceNumber());
            hotelService.attachPayment(reservation.getReservationId(), payment);
            reservation.setPayment(payment);

            if (payment.getStatus() == com.hotel.model.PaymentStatus.FAILED) {
                hotelService.cancelReservation(reservation.getReservationId());
                JOptionPane.showMessageDialog(this,
                        "Payment failed (simulated decline). Your reservation was not confirmed.",
                        "Payment Declined", JOptionPane.ERROR_MESSAGE);
                return;
            }

            bookingCompleted = true;
            ReceiptDialog receipt = new ReceiptDialog((Frame) getOwner(), reservation, room);
            dispose();
            receipt.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel labeled(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.NAVY);
        return l;
    }

    private JLabel boldRow(String label, String value) {
        JLabel l = new JLabel(label + ":   " + value);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }
}
