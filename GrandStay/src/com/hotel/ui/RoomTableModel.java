package com.hotel.ui;

import com.hotel.model.Room;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Table model backing room search results and the admin inventory table. */
public class RoomTableModel extends AbstractTableModel {

    private final String[] columns = {"Room #", "Type", "Floor", "Price / Night", "Status"};
    private List<Room> rooms = new ArrayList<>();

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
        fireTableDataChanged();
    }

    public Room getRoomAt(int row) {
        return rooms.get(row);
    }

    @Override
    public int getRowCount() {
        return rooms.size();
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
        Room room = rooms.get(rowIndex);
        switch (columnIndex) {
            case 0: return room.getRoomNumber();
            case 1: return room.getType().getDisplayName();
            case 2: return room.getFloor();
            case 3: return String.format("$%.2f", room.getPricePerNight());
            case 4: return room.isActive() ? "Available" : "Inactive";
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
