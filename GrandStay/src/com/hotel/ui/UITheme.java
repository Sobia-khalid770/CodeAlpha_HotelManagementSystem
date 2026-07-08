package com.hotel.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.RenderingHints;

/** Centralized colors, fonts, and small factory helpers so every panel looks consistent. */
public final class UITheme {
    private UITheme() {}

    public static final Color NAVY = HotelLogo.NAVY;
    public static final Color NAVY_DARK = HotelLogo.NAVY_DARK;
    public static final Color GOLD = HotelLogo.GOLD;
    public static final Color GOLD_LIGHT = HotelLogo.GOLD_LIGHT;
    public static final Color CREAM = HotelLogo.CREAM;
    public static final Color BG = new Color(0xF4F5F7);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x23262B);
    public static final Color TEXT_MUTED = new Color(0x6B7280);
    public static final Color SUCCESS = new Color(0x1E8E5A);
    public static final Color DANGER = new Color(0xC0392B);
    public static final Color BORDER = new Color(0xE2E4E9);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, NAVY, Color.WHITE, NAVY_DARK);
        return b;
    }

    public static JButton goldButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, GOLD, NAVY_DARK, GOLD_LIGHT);
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, DANGER, Color.WHITE, new Color(0xE74C3C));
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY_BOLD);
        b.setForeground(NAVY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final Color[] current = {Color.WHITE};

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // White fill
                g2.setColor(current[0]);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                // Navy border
                g2.setColor(NAVY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, c.getWidth() - 2, c.getHeight() - 2, 8, 8);
                g2.dispose();
                super.paint(g, c);
            }
        });

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                current[0] = new Color(0xEEF2FF);
                b.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                current[0] = Color.WHITE;
                b.repaint();
            }
        });
        return b;
    }

    private static void styleButton(JButton b, Color bg, Color fg, Color hover) {
        b.setFont(FONT_BODY_BOLD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);       // remove L&F border so our insets control padding
        b.setContentAreaFilled(false);   // we paint the fill ourselves in paintComponent
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setForeground(fg);

        // Track hover state; array so the anonymous classes can write to it
        final Color[] current = {bg};

        // Swap in a BasicButtonUI subclass that paints the fill itself — the only
        // reliable way to force a background color on JButton across all L&Fs
        // (Windows, macOS Aqua and GTK all ignore setBackground on JButton).
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fill rounded rectangle with current color
                g2.setColor(current[0]);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                g2.dispose();
                // Let BasicButtonUI draw the text/icon on top
                super.paint(g, c);
            }
        });

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                current[0] = hover;
                b.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                current[0] = bg;
                b.repaint();
            }
        });
    }

    public static Border card() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18));
    }

    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD_BG);
        p.setBorder(card());
        return p;
    }

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SECTION);
        l.setForeground(NAVY);
        return l;
    }

    public static JLabel bodyLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
    }

    public static void styleComboBox(JComboBox<?> box) {
        box.setFont(FONT_BODY);
        box.setBackground(Color.WHITE);
    }

    public static JLabel pill(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(FONT_SMALL);
        l.setBorder(new EmptyBorder(4, 10, 4, 10));
        return l;
    }
}
