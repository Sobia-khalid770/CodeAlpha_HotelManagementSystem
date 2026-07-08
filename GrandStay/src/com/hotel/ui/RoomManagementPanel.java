package com.hotel.ui;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.HotelService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Tab 3: admin view of the full room inventory, with add/deactivate controls. */
public class RoomManagementPanel extends JPanel {

    private final HotelService hotelService;
    private final Runnable onChange;

    private RoomTableModel tableModel;
    private JTable table;
    private JLabel countLabel;
    private JButton removeButton;

    public RoomManagementPanel(HotelService hotelService, Runnable onChange) {
        this.hotelService = hotelService;
        this.onChange = onChange;
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        setBackground(UITheme.BG);

        add(buildAddCard(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildAddCard() {
        JPanel card = UITheme.card(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 5;
        card.add(UITheme.sectionTitle("Room Inventory Management"), gc);
        gc.gridwidth = 1;

        gc.gridy = 1;
        gc.gridx = 0; card.add(UITheme.bodyLabel("Room Number"), gc);
        gc.gridx = 1; card.add(UITheme.bodyLabel("Type"), gc);
        gc.gridx = 2; card.add(UITheme.bodyLabel("Floor"), gc);
        gc.gridx = 3; card.add(UITheme.bodyLabel("Price / Night"), gc);

        gc.gridy = 2;
        JTextField numberField = new JTextField(6);
        UITheme.styleTextField(numberField);
        gc.gridx = 0; card.add(numberField, gc);

        JComboBox<String> typeCombo = new JComboBox<>();
        for (RoomType t : RoomType.values()) typeCombo.addItem(t.getDisplayName());
        UITheme.styleComboBox(typeCombo);
        gc.gridx = 1; card.add(typeCombo, gc);

        JTextField floorField = new JTextField(4);
        UITheme.styleTextField(floorField);
        gc.gridx = 2; card.add(floorField, gc);

        JTextField priceField = new JTextField(8);
        UITheme.styleTextField(priceField);
        typeCombo.addActionListener(e ->
                priceField.setText(String.valueOf(RoomType.values()[typeCombo.getSelectedIndex()].getBaseRate())));
        priceField.setText(String.valueOf(RoomType.STANDARD.getBaseRate()));
        gc.gridx = 3; card.add(priceField, gc);

        JButton addButton = UITheme.primaryButton("Add Room");
        gc.gridx = 4;
        card.add(addButton, gc);

        addButton.addActionListener(e -> {
            try {
                int number = Integer.parseInt(numberField.getText().trim());
                int floor = Integer.parseInt(floorField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                RoomType type = RoomType.values()[typeCombo.getSelectedIndex()];
                hotelService.addRoom(number, type, floor, price);
                numberField.setText("");
                floorField.setText("");
                refresh();
                if (onChange != null) onChange.run();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Room number, floor, and price must be valid numbers.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = UITheme.card(new BorderLayout(0, 10));

        countLabel = new JLabel();
        countLabel.setFont(UITheme.FONT_SMALL);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        card.add(countLabel, BorderLayout.NORTH);

        tableModel = new RoomTableModel();
        table = new JTable(tableModel);
        SearchBookPanel.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        card.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        removeButton = UITheme.dangerButton("Deactivate Room");
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> removeSelected());
        footer.add(removeButton);
        card.add(footer, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) removeButton.setEnabled(table.getSelectedRow() >= 0);
        });

        return card;
    }

    public void refresh() {
        var rooms = hotelService.getAllRooms();
        tableModel.setRooms(rooms);
        countLabel.setText(rooms.size() + " active room(s) in inventory");
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Room room = tableModel.getRoomAt(row);
        int choice = JOptionPane.showConfirmDialog(this,
                "Deactivate Room " + room.getRoomNumber() + "? It will no longer appear in search results.",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        hotelService.removeRoom(room.getRoomNumber());
        refresh();
        if (onChange != null) onChange.run();
    }
}
