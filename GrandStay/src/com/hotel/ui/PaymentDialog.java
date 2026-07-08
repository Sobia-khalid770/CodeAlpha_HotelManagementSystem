package com.hotel.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simulates a payment collection screen: method selection + card/reference
 * number entry. No real transaction is performed — this purely models the
 * UX of a checkout step for the reservation workflow.
 */
public class PaymentDialog extends JDialog {

    private boolean confirmed = false;
    private String selectedMethod = "Credit Card";
    private String referenceNumber = "";

    private JComboBox<String> methodCombo;
    private JTextField referenceField;
    private JTextField nameOnCardField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JPanel cardFieldsPanel;

    public PaymentDialog(Frame owner, double amount) {
        super(owner, "Payment", true);
        setSize(420, 480);
        setMinimumSize(new Dimension(380, 380));
        setLocationRelativeTo(owner);
        setResizable(true);
        buildUI(amount);
    }

    public boolean wasConfirmed() {
        return confirmed;
    }

    public String getSelectedMethod() {
        return selectedMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    private void buildUI(double amount) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(new EmptyBorder(20, 24, 20, 24));
        root.setBackground(UITheme.BG);
        setContentPane(root);

        JLabel title = UITheme.sectionTitle("Simulated Payment");
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.BG);

        JPanel amountCard = UITheme.card(new BorderLayout());
        JLabel amountLabel = new JLabel(String.format("Amount Due: $%.2f", amount));
        amountLabel.setFont(UITheme.FONT_TITLE);
        amountLabel.setForeground(UITheme.NAVY);
        amountCard.add(amountLabel, BorderLayout.CENTER);
        amountCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(amountCard);
        form.add(Box.createVerticalStrut(14));

        JLabel methodLabel = new JLabel("Payment Method");
        methodLabel.setFont(UITheme.FONT_BODY_BOLD);
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(methodLabel);
        form.add(Box.createVerticalStrut(4));

        methodCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash"});
        UITheme.styleComboBox(methodCombo);
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(methodCombo);
        form.add(Box.createVerticalStrut(12));

        cardFieldsPanel = new JPanel();
        cardFieldsPanel.setLayout(new BoxLayout(cardFieldsPanel, BoxLayout.Y_AXIS));
        cardFieldsPanel.setBackground(UITheme.BG);
        cardFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameOnCardField = addField(cardFieldsPanel, "Name on Card");
        referenceField = addField(cardFieldsPanel, "Card Number (simulated — any digits)");
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setBackground(UITheme.BG);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        expiryField = new JTextField("12/29");
        UITheme.styleTextField(expiryField);
        cvvField = new JTextField("123");
        UITheme.styleTextField(cvvField);
        JPanel expiryWrap = labeledWrap("Expiry (MM/YY)", expiryField);
        JPanel cvvWrap = labeledWrap("CVV", cvvField);
        row.add(expiryWrap);
        row.add(cvvWrap);
        cardFieldsPanel.add(row);

        form.add(cardFieldsPanel);

        methodCombo.addActionListener(e -> {
            boolean isCard = !"Cash".equals(methodCombo.getSelectedItem());
            cardFieldsPanel.setVisible(isCard);
        });

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(UITheme.BG);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(formScroll, BorderLayout.CENTER);

        JLabel disclaimer = new JLabel("<html><i>This is a simulated payment for demo purposes. No real charge occurs.</i></html>");
        disclaimer.setFont(UITheme.FONT_SMALL);
        disclaimer.setForeground(UITheme.TEXT_MUTED);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(UITheme.BG);
        JButton cancel = UITheme.secondaryButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton pay = UITheme.goldButton("Confirm Payment");
        pay.addActionListener(e -> handlePay());
        buttons.add(cancel);
        buttons.add(pay);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BG);
        south.add(disclaimer, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);
    }

    private JTextField addField(JPanel container, String label) {
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(l);
        container.add(Box.createVerticalStrut(4));
        JTextField field = new JTextField();
        UITheme.styleTextField(field);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        container.add(field);
        container.add(Box.createVerticalStrut(10));
        return field;
    }

    private JPanel labeledWrap(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(UITheme.BG);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BODY_BOLD);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void handlePay() {
        selectedMethod = (String) methodCombo.getSelectedItem();
        if (!"Cash".equals(selectedMethod)) {
            if (nameOnCardField.getText().trim().isEmpty() || referenceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete the card details.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            referenceNumber = referenceField.getText().trim();
        } else {
            referenceNumber = "";
        }
        confirmed = true;
        dispose();
    }
}
