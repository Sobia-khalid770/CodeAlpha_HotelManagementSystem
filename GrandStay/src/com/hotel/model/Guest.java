package com.hotel.model;

/** A simple value object holding the details of the person making a booking. */
public class Guest {
    private String fullName;
    private String email;
    private String phone;

    public Guest(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** Escapes commas so a guest name/email can't break the CSV format. */
    public String toCsv() {
        return escape(fullName) + "|" + escape(email) + "|" + escape(phone);
    }

    public static Guest fromCsv(String token) {
        String[] parts = token.split("\\|", -1);
        return new Guest(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]));
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace(",", "\u2063").replace("|", "\u2064");
    }

    private static String unescape(String s) {
        return s.replace("\u2063", ",").replace("\u2064", "|");
    }

    @Override
    public String toString() {
        return fullName + " <" + email + ">";
    }
}
