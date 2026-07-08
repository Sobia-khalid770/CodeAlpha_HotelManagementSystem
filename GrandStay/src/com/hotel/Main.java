package com.hotel;

import com.hotel.service.HotelService;
import com.hotel.storage.FileStorageManager;
import com.hotel.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default cross-platform look and feel.
        }

        SwingUtilities.invokeLater(() -> {
            FileStorageManager storage = new FileStorageManager("data");
            HotelService hotelService = new HotelService(storage);
            MainFrame frame = new MainFrame(hotelService);
            frame.setVisible(true);
        });
    }
}
