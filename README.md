<p align="center">
  <img src="https://raw.githubusercontent.com/Sobia-khalid770/CodeAlpha_HotelManagementSystem/master/GrandStay/logo_banner.png" alt="GrandStay Hotel Reservation System" width="420"/>
</p>

<h1 align="center">GrandStay — Hotel Reservation System</h1>

<p align="center">
  A full-featured desktop hotel management application built entirely in Java Swing — no frameworks, no external libraries, just pure Java SE.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=java" />
  <img src="https://img.shields.io/badge/UI-Java%20Swing-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/Storage-CSV%20File%20I%2FO-green?style=flat-square" />
  <img src="https://img.shields.io/badge/Dependencies-None-lightgrey?style=flat-square" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" />
</p>

---

## 📌 Overview

GrandStay is a desktop hotel reservation system that covers the full lifecycle of a hotel booking — from searching available rooms and making a reservation, to processing simulated payments and managing the hotel's room inventory. It was built as an OOP coursework project demonstrating clean layered architecture, event-driven GUI design, and file-based persistence.

Everything runs from a single JAR — no database, no internet connection, no setup beyond having Java installed.

---

## ✨ App Icon

<p align="center">
  <img src="https://raw.githubusercontent.com/Sobia-khalid770/CodeAlpha_HotelManagementSystem/master/GrandStay/logo_mark_512.png" alt="GrandStay Mark" width="120"/>
</p>

> The logo and wordmark are drawn entirely in code using `Graphics2D` in `HotelLogo.java` — no image files are needed at runtime. The PNGs in this repo are exports for reference only.

---

## ✨ Features

### 🔍 Search & Book
- Filter rooms by **type** (Standard / Deluxe / Suite) and **date range**
- Live availability check — only shows rooms free for your selected dates
- Guided multi-step booking dialog: guest details → price summary → payment
- Booking confirmation with full receipt

### 📋 Manage Reservations
- View all reservations in a sortable table
- Filter by status: **Confirmed**, **Cancelled**, or **Completed**
- Cancel any active reservation — payment is automatically marked as refunded and the room is freed up for those dates
- View detailed receipt for any booking

### 🏨 Room Management
- Add new rooms with custom room number, type, floor, and nightly price
- Deactivate rooms to remove them from search results without deleting history
- Live room count display

### 💳 Payment Simulation
- Supports **Credit Card**, **Debit Card**, and **Cash**
- Simulates gateway approval and decline
- Masked card number + reference number on confirmation
- > 💡 Enter a card number ending in `0000` to simulate a declined payment

### 🎨 Custom UI
- Fully custom-branded dark navy and gold theme
- Logo drawn in `Graphics2D` — crisp at any screen resolution
- Custom `BasicButtonUI` renderer ensures correct button colors on all operating systems (Windows, macOS, Linux)
- Cross-platform table header renderer — dark navy column headers with white text on all Look & Feels

---

## 🚀 Getting Started

### Requirements
- **Java 17 or higher** — [Download here](https://adoptium.net)

### ▶ Option 1 — Run the prebuilt JAR *(fastest)*

```bash
java -jar GrandStay-Hotel-Reservation.jar
```

A `data/` folder is created automatically next to the JAR on first run. It seeds 12 demo rooms across 3 floors.

### 🔨 Option 2 — Build from source

```bash
# 1. Compile
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt

# 2. Run
java -cp out com.hotel.Main
```

### 💡 Option 3 — Open in Eclipse

1. **File** → **Import** → **Existing Projects into Workspace**
2. Select the project root folder
3. Right-click the project → **Run As** → **Java Application**
4. Select `com.hotel.Main` as the main class

---


## 📁 Project Structure

```
CodeAlpha_HotelManagementSystem/
└── GrandStay/
    ├── GrandStay-Hotel-Reservation.jar     Prebuilt runnable JAR
    ├── logo_banner.png                     Exported wordmark PNG
    ├── logo_mark_512.png                   Exported icon PNG
    ├── manifest.txt                        JAR manifest
    └── src/com/hotel/
        │
        ├── Main.java                       Entry point
        │
        ├── model/                          Plain data classes (no Swing, no I/O)
        │   ├── Room.java                   Room number, type, floor, price, active flag
        │   ├── RoomType.java               Enum: STANDARD ($79.99) / DELUXE ($129.99) / SUITE ($219.99)
        │   ├── Guest.java                  Guest full name + contact details
        │   ├── Reservation.java            Links guest ↔ room ↔ date range ↔ payment
        │   ├── ReservationStatus.java      Enum: CONFIRMED / CANCELLED / COMPLETED
        │   ├── Payment.java                Payment method, amount, status, reference
        │   └── PaymentStatus.java          Enum: PENDING / SUCCESS / FAILED / REFUNDED
        │
        ├── service/                        Business logic layer
        │   ├── HotelService.java           Search, book, cancel, validate
        │   └── PaymentSimulator.java       Simulates gateway approval / decline
        │
        ├── storage/                        Persistence layer
        │   └── FileStorageManager.java     Reads and writes rooms.csv + reservations.csv
        │
        └── ui/                             Swing presentation layer
            ├── MainFrame.java              Top-level JFrame — header + three-tab layout
            ├── HeaderPanel.java            Branded banner with logo and tagline
            ├── HotelLogo.java              Graphics2D logo renderer
            ├── UITheme.java                Colors, fonts, button and field factories
            ├── SearchBookPanel.java        Tab 1: room search and booking
            ├── BookingDialog.java          Multi-step booking dialog
            ├── PaymentDialog.java          Payment method selection and card entry
            ├── ReceiptDialog.java          Booking confirmation receipt
            ├── ManageReservationsPanel.java Tab 2: reservation list and cancellation
            ├── RoomManagementPanel.java    Tab 3: room inventory management
            ├── RoomTableModel.java         AbstractTableModel for room data
            └── ReservationTableModel.java  AbstractTableModel for reservation data
```

**23 Java files · ~3,800 lines of code · 0 external dependencies**

---

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│              UI Layer               │  Swing panels, dialogs, table models
└────────────────┬────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│            Service Layer            │  Business rules, validation
└────────────────┬────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│            Storage Layer            │  CSV file read / write
└────────────────┬────────────────────┘
                 ▼
┌─────────────────────────────────────┐
│            Model Layer              │  Pure data — no dependencies
└─────────────────────────────────────┘
```

---

## 💾 Persistence

```
data/
├── rooms.csv           Room number, type, floor, price, active status
└── reservations.csv    Reservation ID, guest, room, dates, status, payment
```

- Saved on every change — bookings survive restarts
- **To reset:** delete the `data/` folder. App reseeds 12 demo rooms on next launch.

---

## 🧪 Demo Walkthrough

1. Launch the app — 12 rooms are pre-loaded
2. **Search & Book** — pick Suite, set dates, click Search, select a room, click Book
3. Fill in guest name and email
4. Choose Credit Card — enter any 16-digit number (ending in `0000` = declined)
5. Receipt dialog shows your booking reference
6. **Manage Reservations** — your booking appears; cancel it to test refund flow
7. **Room Management** — add Room `401`, Deluxe, Floor 4 — appears in search instantly

---

## 👩‍💻 Author

**Sobia Khalid**
BS Artificial Intelligence — NUML Islamabad
CodeAlpha Internship Project

---

## 📄 License

MIT License — free to use, modify, and distribute for personal or academic purposes.
