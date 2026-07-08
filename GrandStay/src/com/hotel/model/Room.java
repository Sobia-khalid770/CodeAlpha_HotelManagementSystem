package com.hotel.model;

import java.util.Objects;

/**
 * Represents a physical hotel room. Availability here means "not currently
 * blocked" — actual date-range conflicts are resolved by checking active
 * reservations, so a room can be generally available yet booked for a
 * specific date window.
 */
public class Room {

    private final int roomNumber;
    private RoomType type;
    private int floor;
    private double pricePerNight;
    private boolean active; // false = room removed from inventory (soft delete)

    public Room(int roomNumber, RoomType type, int floor, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.floor = floor;
        this.pricePerNight = pricePerNight;
        this.active = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /** Serializes this room to a single CSV line for file storage. */
    public String toCsv() {
        return String.join(",",
                String.valueOf(roomNumber),
                type.name(),
                String.valueOf(floor),
                String.valueOf(pricePerNight),
                String.valueOf(active));
    }

    /** Parses a Room back from a CSV line produced by {@link #toCsv()}. */
    public static Room fromCsv(String line) {
        String[] parts = line.split(",");
        Room room = new Room(
                Integer.parseInt(parts[0]),
                RoomType.valueOf(parts[1]),
                Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3]));
        if (parts.length > 4) {
            room.setActive(Boolean.parseBoolean(parts[4]));
        }
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return roomNumber == room.roomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type.getDisplayName() + ", Floor " + floor + ")";
    }
}
