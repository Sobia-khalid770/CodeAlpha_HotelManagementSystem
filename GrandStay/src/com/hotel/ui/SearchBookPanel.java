package com.hotel.ui;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.HotelService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Tab 1: search available rooms by type + date range, then launch the booking flow. */
public class SearchBookPanel extends JPanel {

    private final HotelService hotelService;
    private final Runnable onReservationMade;

    private JComboBox<String> typeCombo;
    private JSpinner checkInSpinner;
    private JSpinner checkOutSpinner;
    private JTable resultsTable;
    private RoomTableModel tableModel;
    private JLabel resultCountLabel;
    private JButton bookButton;

    public SearchBookPanel(HotelService hotelService, Runnable onReservationMade) {
        this.hotelService = hotelService;
        this.onReservationMade = onReservationMade;
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        setBackground(UITheme.BG);

        add(buildSearchCard(), BorderLayout.NORTH);
        add(buildResultsCard(), BorderLayout.CENTER);

        performSearch();
    }

    private JPanel buildSearchCard() {
        JPanel card = UITheme.card(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;

        JLabel title = UITheme.sectionTitle("Find a Room");
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 6;
        card.add(title, gc);
        gc.gridwidth = 1;

        gc.gridy = 1;
        gc.gridx = 0; card.add(UITheme.bodyLabel("Room Type"), gc);
        gc.gridx = 1; card.add(UITheme.bodyLabel("Check-In"), gc);
        gc.gridx = 2; card.add(UITheme.bodyLabel("Check-Out"), gc);

        gc.gridy = 2;
        typeCombo = new JComboBox<>();
        typeCombo.addItem("Any Type");
        for (RoomType t : RoomType.values()) typeCombo.addItem(t.getDisplayName());
        UITheme.styleComboBox(typeCombo);
        gc.gridx = 0; card.add(typeCombo, gc);

        Date today = new Date();
        Date maxDate = toDate(LocalDate.now().plusYears(2));
        checkInSpinner = new JSpinner(new SpinnerDateModel(tomorrow(), today, maxDate, Calendar.DAY_OF_MONTH));
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "dd MMM yyyy"));
        gc.gridx = 1; card.add(checkInSpinner, gc);

        checkOutSpinner = new JSpinner(new SpinnerDateModel(dayAfter(tomorrow()), today, maxDate, Calendar.DAY_OF_MONTH));
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "dd MMM yyyy"));
        gc.gridx = 2; card.add(checkOutSpinner, gc);

        JButton searchButton = UITheme.primaryButton("Search Availability");
        searchButton.addActionListener(e -> performSearch());
        gc.gridx = 3; card.add(searchButton, gc);

        return card;
    }

    private JPanel buildResultsCard() {
        JPanel card = UITheme.card(new BorderLayout(0, 10));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JLabel title = UITheme.sectionTitle("Available Rooms");
        resultCountLabel = new JLabel();
        resultCountLabel.setFont(UITheme.FONT_SMALL);
        resultCountLabel.setForeground(UITheme.TEXT_MUTED);
        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(resultCountLabel, BorderLayout.EAST);
        card.add(headerRow, BorderLayout.NORTH);

        tableModel = new RoomTableModel();
        resultsTable = new JTable(tableModel);
        styleTable(resultsTable);
        JScrollPane scroll = new JScrollPane(resultsTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        card.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        bookButton = UITheme.goldButton("Book Selected Room");
        bookButton.setEnabled(false);
        bookButton.addActionListener(e -> openBookingDialog());
        footer.add(bookButton);
        card.add(footer, BorderLayout.SOUTH);

        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                bookButton.setEnabled(resultsTable.getSelectedRow() >= 0);
            }
        });

        return card;
    }

    static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(UITheme.FONT_BODY);
        table.setSelectionBackground(new Color(0xDCE6F5));
        table.setSelectionForeground(UITheme.TEXT_DARK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(UITheme.NAVY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Custom renderer: forces dark-navy background + white bold text on every
        // column header, overriding whatever the OS Look-and-Feel would otherwise
        // paint. Without this many L&Fs (Windows, macOS Aqua) ignore setBackground
        // on JTableHeader entirely.
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col);

                lbl.setFont(UITheme.FONT_BODY_BOLD);
                lbl.setBackground(UITheme.NAVY);
                lbl.setForeground(Color.WHITE);
                lbl.setOpaque(true);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        // thin right separator between columns
                        BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.NAVY_DARK),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return lbl;
            }
        });
    }

    private void performSearch() {
        try {
            RoomType selectedType = typeCombo.getSelectedIndex() == 0 ? null
                    : RoomType.values()[typeCombo.getSelectedIndex() - 1];
            LocalDate checkIn = toLocalDate((Date) checkInSpinner.getValue());
            LocalDate checkOut = toLocalDate((Date) checkOutSpinner.getValue());
            List<Room> available = hotelService.searchAvailableRooms(selectedType, checkIn, checkOut);
            tableModel.setRooms(available);
            resultCountLabel.setText(available.size() + " room(s) found for the selected dates");
            bookButton.setEnabled(false);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Search", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openBookingDialog() {
        int row = resultsTable.getSelectedRow();
        if (row < 0) return;
        Room room = tableModel.getRoomAt(row);
        LocalDate checkIn = toLocalDate((Date) checkInSpinner.getValue());
        LocalDate checkOut = toLocalDate((Date) checkOutSpinner.getValue());

        Window owner = SwingUtilities.getWindowAncestor(this);
        BookingDialog dialog = new BookingDialog((Frame) owner, hotelService, room, checkIn, checkOut);
        dialog.setVisible(true);
        if (dialog.wasBookingCompleted()) {
            performSearch();
            if (onReservationMade != null) onReservationMade.run();
        }
    }

    private static Date tomorrow() {
        return toDate(LocalDate.now().plusDays(1));
    }

    private static Date dayAfter(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, 2);
        return c.getTime();
    }

    private static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
