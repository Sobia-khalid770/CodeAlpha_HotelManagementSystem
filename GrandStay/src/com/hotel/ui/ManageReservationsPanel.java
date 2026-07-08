package com.hotel.ui;

import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.service.HotelService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/** Tab 2: view all reservations, inspect details, and cancel active bookings. */
public class ManageReservationsPanel extends JPanel {

    private final HotelService hotelService;
    private final Runnable onChange;

    private ReservationTableModel tableModel;
    private JTable table;
    private JComboBox<String> filterCombo;
    private JButton cancelButton;
    private JButton detailsButton;
    private JLabel countLabel;

    public ManageReservationsPanel(HotelService hotelService, Runnable onChange) {
        this.hotelService = hotelService;
        this.onChange = onChange;
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        setBackground(UITheme.BG);

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildToolbar() {
        JPanel card = UITheme.card(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(UITheme.sectionTitle("All Reservations"));

        filterCombo = new JComboBox<>(new String[]{"All Statuses", "Confirmed", "Cancelled", "Completed"});
        UITheme.styleComboBox(filterCombo);
        filterCombo.addActionListener(e -> refresh());

        JButton refreshButton = UITheme.secondaryButton("Refresh");
        refreshButton.addActionListener(e -> refresh());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(filterCombo);
        right.add(refreshButton);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = UITheme.card(new BorderLayout(0, 10));

        countLabel = new JLabel();
        countLabel.setFont(UITheme.FONT_SMALL);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        card.add(countLabel, BorderLayout.NORTH);

        tableModel = new ReservationTableModel();
        table = new JTable(tableModel);
        SearchBookPanel.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        card.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        detailsButton = UITheme.secondaryButton("View Details");
        detailsButton.setEnabled(false);
        detailsButton.addActionListener(e -> showDetails());
        cancelButton = UITheme.dangerButton("Cancel Reservation");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelSelected());
        footer.add(detailsButton);
        footer.add(cancelButton);
        card.add(footer, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        return card;
    }

    private void updateButtonStates() {
        int row = table.getSelectedRow();
        detailsButton.setEnabled(row >= 0);
        if (row >= 0) {
            Reservation r = tableModel.getReservationAt(row);
            cancelButton.setEnabled(r.getStatus() == ReservationStatus.CONFIRMED);
        } else {
            cancelButton.setEnabled(false);
        }
    }

    public void refresh() {
        List<Reservation> all = hotelService.getAllReservations();
        String filter = (String) filterCombo.getSelectedItem();
        List<Reservation> filtered = all.stream().filter(r -> {
            if (filter == null || filter.equals("All Statuses")) return true;
            return r.getStatus().name().equalsIgnoreCase(filter);
        }).collect(java.util.stream.Collectors.toList());
        tableModel.setReservations(filtered);
        countLabel.setText(filtered.size() + " reservation(s)");
        updateButtonStates();
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Reservation r = tableModel.getReservationAt(row);
        hotelService.findRoom(r.getRoomNumber()).ifPresent(room -> {
            ReceiptDialog dialog = new ReceiptDialog((Frame) SwingUtilities.getWindowAncestor(this), r, room);
            dialog.setVisible(true);
        });
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Reservation r = tableModel.getReservationAt(row);
        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel reservation " + r.getReservationId() + " for " + r.getGuest().getFullName() + "?\n"
                        + "Any successful payment will be marked as refunded.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            hotelService.cancelReservation(r.getReservationId());
            JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.",
                    "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            refresh();
            if (onChange != null) onChange.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not cancel: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
