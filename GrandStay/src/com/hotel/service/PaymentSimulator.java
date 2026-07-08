package com.hotel.service;

import com.hotel.model.Payment;
import com.hotel.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Simulates a payment gateway. No real transaction ever occurs; this exists
 * to model the booking workflow end-to-end (method + reference -> processing
 * -> success/failure) so the rest of the system can react to a Payment the
 * same way it would with a real integration.
 */
public class PaymentSimulator {

    /**
     * "Processes" a payment. A very small, deterministic failure chance is
     * simulated for card numbers ending in 0000 so the UI has something
     * realistic to demonstrate; everything else succeeds.
     */
    public Payment process(double amount, String method, String cardOrRefNumber) {
        String masked = maskReference(method, cardOrRefNumber);
        boolean simulatedFailure = cardOrRefNumber != null && cardOrRefNumber.replaceAll("\\s", "").endsWith("0000");

        PaymentStatus status = simulatedFailure ? PaymentStatus.FAILED : PaymentStatus.SUCCESS;
        String id = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Payment(id, amount, method, masked, status, LocalDateTime.now());
    }

    private String maskReference(String method, String raw) {
        if ("Cash".equalsIgnoreCase(method)) {
            return "CASH ON ARRIVAL";
        }
        if (raw == null || raw.isBlank()) {
            return "N/A";
        }
        String digitsOnly = raw.replaceAll("\\D", "");
        if (digitsOnly.length() < 4) {
            return "**** " + digitsOnly;
        }
        String last4 = digitsOnly.substring(digitsOnly.length() - 4);
        return "**** **** **** " + last4;
    }
}
