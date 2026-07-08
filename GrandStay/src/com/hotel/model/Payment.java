package com.hotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simulated payment transaction. No real payment gateway is involved —
 * this models the workflow (method selection, processing, success/failure)
 * that a real integration would slot into later.
 */
public class Payment {
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String paymentId;
    private final double amount;
    private final String method; // e.g. "Credit Card", "Debit Card", "Cash"
    private final String maskedReference; // e.g. "**** **** **** 1234" or "CASH"
    private final PaymentStatus status;
    private final LocalDateTime timestamp;

    public Payment(String paymentId, double amount, String method, String maskedReference,
                    PaymentStatus status, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.maskedReference = maskedReference;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public String getMaskedReference() {
        return maskedReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String toCsv() {
        return String.join(",",
                paymentId,
                String.valueOf(amount),
                method,
                maskedReference.replace(",", " "),
                status.name(),
                timestamp.format(TS_FORMAT));
    }

    public static Payment fromCsv(String token) {
        String[] p = token.split(",", -1);
        return new Payment(p[0], Double.parseDouble(p[1]), p[2], p[3],
                PaymentStatus.valueOf(p[4]), LocalDateTime.parse(p[5], TS_FORMAT));
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f via %s (%s)", paymentId, amount, method, status);
    }
}
