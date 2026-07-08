package com.hotel.storage;

import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all File I/O for the application. Rooms and reservations are
 * persisted as plain-text CSV files under a data directory, which keeps the
 * project dependency-free while still demonstrating durable storage that
 * survives application restarts.
 *
 * The class is intentionally the ONLY place that touches disk, so the rest
 * of the app can be tested/reasoned about without worrying about I/O.
 */
public class FileStorageManager {

    private static final Logger LOGGER = Logger.getLogger(FileStorageManager.class.getName());

    private final Path dataDir;
    private final Path roomsFile;
    private final Path reservationsFile;

    public FileStorageManager(String dataDirectoryPath) {
        this.dataDir = Paths.get(dataDirectoryPath);
        this.roomsFile = dataDir.resolve("rooms.csv");
        this.reservationsFile = dataDir.resolve("reservations.csv");
        ensureStorageExists();
    }

    private void ensureStorageExists() {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            if (!Files.exists(roomsFile)) {
                Files.createFile(roomsFile);
            }
            if (!Files.exists(reservationsFile)) {
                Files.createFile(reservationsFile);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to initialize data directory: " + dataDir, e);
            throw new UncheckedIOException(e);
        }
    }

    public synchronized List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(roomsFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    rooms.add(Room.fromCsv(line));
                } catch (Exception parseEx) {
                    LOGGER.warning("Skipping malformed room record: " + line);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load rooms", e);
        }
        return rooms;
    }

    public synchronized void saveRooms(List<Room> rooms) {
        try (BufferedWriter writer = Files.newBufferedWriter(roomsFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Room room : rooms) {
                writer.write(room.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save rooms", e);
            throw new UncheckedIOException(e);
        }
    }

    public synchronized List<Reservation> loadReservations() {
        List<Reservation> reservations = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(reservationsFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    reservations.add(Reservation.fromCsv(line));
                } catch (Exception parseEx) {
                    LOGGER.warning("Skipping malformed reservation record: " + line);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load reservations", e);
        }
        return reservations;
    }

    public synchronized void saveReservations(List<Reservation> reservations) {
        try (BufferedWriter writer = Files.newBufferedWriter(reservationsFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Reservation reservation : reservations) {
                writer.write(reservation.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save reservations", e);
            throw new UncheckedIOException(e);
        }
    }

    public Path getDataDir() {
        return dataDir;
    }
}
