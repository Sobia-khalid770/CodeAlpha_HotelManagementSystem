package com.hotel.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Top banner: renders the GrandStay logo lockup plus a live clock/date. */
public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 78));
        setBackground(UITheme.NAVY_DARK);

        JLabel logoLabel = new JLabel(new ImageIcon(HotelLogo.renderBanner(420, 78)));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(logoLabel);
        add(left, BorderLayout.WEST);

        JLabel status = new JLabel("● System Online", SwingConstants.RIGHT);
        status.setForeground(new Color(0x7CD992));
        status.setFont(UITheme.FONT_BODY_BOLD);
        status.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));
        add(status, BorderLayout.EAST);
    }
}
