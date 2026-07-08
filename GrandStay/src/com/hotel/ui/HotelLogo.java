package com.hotel.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Generates the application's logo entirely in code via Graphics2D — a
 * rounded navy badge containing a minimalist building silhouette with a
 * gold key/star accent, plus the "GrandStay" wordmark. Keeping it code-drawn
 * means the icon is crisp at any resolution and ships with zero binary
 * asset dependencies.
 */
public final class HotelLogo {

    private HotelLogo() {}

    public static final Color NAVY = new Color(0x1B2A4A);
    public static final Color NAVY_DARK = new Color(0x101B33);
    public static final Color GOLD = new Color(0xD4AF37);
    public static final Color GOLD_LIGHT = new Color(0xF0D57A);
    public static final Color CREAM = new Color(0xFAF7F0);

    /** Renders just the badge/mark (no text) at the given pixel size — used for window/taskbar icons. */
    public static BufferedImage renderMark(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        float pad = size * 0.04f;
        RoundRectangle2D badge = new RoundRectangle2D.Float(pad, pad, size - 2 * pad, size - 2 * pad, size * 0.26f, size * 0.26f);

        GradientPaint bgGradient = new GradientPaint(0, 0, NAVY, size, size, NAVY_DARK);
        g.setPaint(bgGradient);
        g.fill(badge);

        g.setStroke(new BasicStroke(Math.max(1f, size * 0.018f)));
        g.setColor(GOLD);
        g.draw(badge);

        drawBuilding(g, size);

        g.dispose();
        return img;
    }

    /** Draws a simple stylized hotel building with a star, centered in a size x size box. */
    private static void drawBuilding(Graphics2D g, int size) {
        double cx = size / 2.0;
        double baseY = size * 0.78;
        double buildingW = size * 0.46;
        double buildingH = size * 0.40;
        double left = cx - buildingW / 2;
        double top = baseY - buildingH;

        // Main tower
        RoundRectangle2D tower = new RoundRectangle2D.Double(left, top, buildingW, buildingH, size * 0.03, size * 0.03);
        g.setColor(CREAM);
        g.fill(tower);

        // Roof triangle (peaked roof accent)
        Path2D roof = new Path2D.Double();
        roof.moveTo(left - size * 0.03, top);
        roof.lineTo(cx, top - size * 0.14);
        roof.lineTo(left + buildingW + size * 0.03, top);
        roof.closePath();
        g.setColor(GOLD);
        g.fill(roof);

        // Windows grid
        g.setColor(NAVY);
        int cols = 3;
        int rows = 3;
        double winMargin = buildingW * 0.16;
        double winAreaW = buildingW - 2 * winMargin;
        double winAreaTop = top + buildingH * 0.14;
        double winAreaH = buildingH * 0.55;
        double gapX = winAreaW * 0.18;
        double gapY = winAreaH * 0.22;
        double winW = (winAreaW - gapX * (cols - 1)) / cols;
        double winH = (winAreaH - gapY * (rows - 1)) / rows;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double wx = left + winMargin + c * (winW + gapX);
                double wy = winAreaTop + r * (winH + gapY);
                g.fill(new RoundRectangle2D.Double(wx, wy, winW, winH, winW * 0.25, winW * 0.25));
            }
        }

        // Door
        double doorW = buildingW * 0.26;
        double doorH = buildingH * 0.22;
        double doorX = cx - doorW / 2;
        double doorY = baseY - doorH;
        g.setColor(NAVY);
        g.fill(new RoundRectangle2D.Double(doorX, doorY, doorW, doorH, doorW * 0.3, doorW * 0.3));

        // Gold star above roof (five-point) for a touch of "5-star hotel" branding
        drawStar(g, cx, top - size * 0.20, size * 0.05);
    }

    private static void drawStar(Graphics2D g, double cx, double cy, double r) {
        Path2D star = new Path2D.Double();
        int points = 5;
        double innerR = r * 0.45;
        for (int i = 0; i < points * 2; i++) {
            double radius = (i % 2 == 0) ? r : innerR;
            double angle = Math.PI / 2 + i * Math.PI / points;
            double x = cx + radius * Math.cos(angle);
            double y = cy - radius * Math.sin(angle);
            if (i == 0) star.moveTo(x, y); else star.lineTo(x, y);
        }
        star.closePath();
        g.setColor(GOLD_LIGHT);
        g.fill(star);
    }

    /** Renders the full horizontal lockup: badge mark + "GrandStay" wordmark + tagline. */
    public static BufferedImage renderBanner(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GradientPaint bg = new GradientPaint(0, 0, NAVY_DARK, width, 0, NAVY);
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);

        int markSize = (int) (height * 0.78);
        int markY = (height - markSize) / 2;
        int markX = (int) (height * 0.28);
        BufferedImage mark = renderMark(markSize);
        g.drawImage(mark, markX, markY, null);

        int textX = markX + markSize + (int) (height * 0.25);
        g.setColor(CREAM);
        g.setFont(new Font("SansSerif", Font.BOLD, (int) (height * 0.30)));
        FontMetrics fm = g.getFontMetrics();
        int titleY = height / 2 - (int) (height * 0.06);
        g.drawString("GrandStay", textX, titleY + fm.getAscent() / 2);

        g.setColor(GOLD_LIGHT);
        g.setFont(new Font("SansSerif", Font.PLAIN, (int) (height * 0.13)));
        g.drawString("HOTEL RESERVATION SYSTEM", textX, titleY + (int) (height * 0.28));

        g.dispose();
        return img;
    }

    public static ImageIcon markIcon(int size) {
        return new ImageIcon(renderMark(size));
    }
}
