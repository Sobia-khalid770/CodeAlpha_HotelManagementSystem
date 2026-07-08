package com.hotel.model;

/**
 * Enumerates the categories of rooms offered by the hotel.
 * Each category carries a base nightly rate and a short marketing description,
 * which keeps pricing rules centralized instead of scattered across the UI.
 */
public enum RoomType {
    STANDARD("Standard", 79.99, "Cozy room with all essential amenities."),
    DELUXE("Deluxe", 129.99, "Spacious room with premium furnishings and a city view."),
    SUITE("Suite", 219.99, "Luxurious multi-room suite with living area and top-tier amenities.");

    private final String displayName;
    private final double baseRate;
    private final String description;

    RoomType(String displayName, double baseRate, String description) {
        this.displayName = displayName;
        this.baseRate = baseRate;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public String getDescription() {
        return description;
    }

    /** Resolves a RoomType from its persisted/display name, defaulting gracefully. */
    public static RoomType fromString(String value) {
        for (RoomType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown room type: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
