package com.hotel.ui;

import com.hotel.service.HotelService;

import javax.swing.*;
import java.awt.*;

/** Top-level application window: header banner + tabbed navigation across the three workflows. */
public class MainFrame extends JFrame {

    private final HotelService hotelService;
    private ManageReservationsPanel reservationsPanel;
    private RoomManagementPanel roomManagementPanel;

    public MainFrame(HotelService hotelService) {
        super("GrandStay — Hotel Reservation System");
        this.hotelService = hotelService;

        setIconImage(HotelLogo.renderMark(64));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setSize(1100, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(new HeaderPanel(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY_BOLD);
        tabs.setBackground(UITheme.BG);

        SearchBookPanel searchPanel = new SearchBookPanel(hotelService, this::refreshAll);
        reservationsPanel = new ManageReservationsPanel(hotelService, this::refreshAll);
        roomManagementPanel = new RoomManagementPanel(hotelService, this::refreshAll);

        tabs.addTab("  Search & Book  ", searchPanel);
        tabs.addTab("  Manage Reservations  ", reservationsPanel);
        tabs.addTab("  Room Inventory  ", roomManagementPanel);

        return tabs;
    }

    private void refreshAll() {
        reservationsPanel.refresh();
        roomManagementPanel.refresh();
    }
}
