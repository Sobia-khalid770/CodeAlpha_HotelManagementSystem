package com.hotel.ui;

import com.hotel.model.Reservation;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Table model backing the "Manage Reservations" view. */
public class ReservationTableModel extends AbstractTableModel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final String[] columns = {"Reservation ID", "Room #", "Guest", "Check-In", "Check-Out", "Nights", "Total", "Status"};
    private List<Reservation> reservations = new ArrayList<>();

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        fireTableDataChanged();
    }

    public Reservation getReservationAt(int row) {
        return reservations.get(row);
    }

    @Override
    public int getRowCount() {
        return reservations.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reservation r = reservations.get(rowIndex);
        switch (columnIndex) {
            case 0: return r.getReservationId();
            case 1: return r.getRoomNumber();
            case 2: return r.getGuest().getFullName();
            case 3: return r.getCheckIn().format(FMT);
            case 4: return r.getCheckOut().format(FMT);
            case 5: return r.getNights();
            case 6: return String.format("$%.2f", r.getTotalAmount());
            case 7: return r.getStatus();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
