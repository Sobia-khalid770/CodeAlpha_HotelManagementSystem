package com.hotel.ui;

import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** Shows a booking confirmation / receipt after a successful payment. */
public class ReceiptDialog extends JDialog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");

    public ReceiptDialog(Frame owner, Reservation reservation, Room room) {
        super(owner, "Booking Confirmed", true);
        setSize(440, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        buildUI(reservation, room);
    }

    private void buildUI(Reservation reservation, Room room) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(UITheme.BG);
        setContentPane(root);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.BG);
        JLabel icon = new JLabel(HotelLogo.markIcon(56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = new JLabel("Booking Confirmed!");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.SUCCESS);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("A confirmation has been recorded in the system.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(icon);
        header.add(Box.createVerticalStrut(8));
        header.add(title);
        header.add(sub);
        root.add(header, BorderLayout.NORTH);

        JPanel details = UITheme.card(new GridLayout(0, 1, 0, 6));
        details.add(row("Reservation ID", reservation.getReservationId()));
        details.add(row("Guest", reservation.getGuest().getFullName()));
        details.add(row("Room", room.getRoomNumber() + " (" + room.getType().getDisplayName() + ")"));
        details.add(row("Check-In", reservation.getCheckIn().format(FMT)));
        details.add(row("Check-Out", reservation.getCheckOut().format(FMT)));
        details.add(row("Nights", String.valueOf(reservation.getNights())));
        Payment payment = reservation.getPayment();
        if (payment != null) {
            details.add(row("Payment Method", payment.getMethod()));
            details.add(row("Reference", payment.getMaskedReference()));
            details.add(row("Payment Status", payment.getStatus().toString()));
        }
        JLabel totalRow = row("Total Paid", String.format("$%.2f", reservation.getTotalAmount()));
        totalRow.setFont(UITheme.FONT_BODY_BOLD.deriveFont(16f));
        totalRow.setForeground(UITheme.NAVY);
        details.add(totalRow);

        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        JButton close = UITheme.primaryButton("Done");
        close.addActionListener(e -> dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UITheme.BG);
        footer.add(close);
        root.add(footer, BorderLayout.SOUTH);
    }

    private JLabel row(String label, String value) {
        JLabel l = new JLabel("<html><span style='color:#6B7280;'>" + label + ":</span>&nbsp; <b>" + value + "</b></html>");
        l.setFont(UITheme.FONT_BODY);
        return l;
    }
}
