# GrandStay — Hotel Reservation System

A desktop Java Swing application for **Task 4: Hotel Reservation System**.

## Features
- **Search & Book** — filter rooms by category (Standard / Deluxe / Suite) and date range, see live availability, book in a guided flow.
- **Room categorization** — `RoomType` enum drives pricing, descriptions, and filtering.
- **Make & cancel reservations** — booking creates a `Reservation`; cancelling auto-refunds any successful payment and frees the room back up for those dates.
- **Payment simulation** — choose Credit Card / Debit Card / Cash, enter (fake) card details, see a simulated approval/decline and a masked reference number.
- **Booking details / receipt view** — confirmation screen and a "View Details" action in Manage Reservations.
- **OOP design** — clean separation into `model` (Room, Guest, Reservation, Payment, enums), `service` (HotelService business rules, PaymentSimulator), `storage` (FileStorageManager), and `ui` (Swing views).
- **File I/O persistence** — rooms and reservations are saved as human-readable CSV files under `data/` and reloaded automatically on startup, so bookings survive a restart.
- **Custom logo** — the "GrandStay" mark and wordmark are drawn entirely in code (`HotelLogo.java`, `Graphics2D`), so there's no external image dependency; it's used as the window/taskbar icon and the in-app header banner. Standalone PNGs are included too.

## Project Structure
```
src/com/hotel/
  Main.java                      entry point
  model/                         Room, RoomType, Guest, Reservation, ReservationStatus, Payment, PaymentStatus
  service/                       HotelService (business rules), PaymentSimulator
  storage/                       FileStorageManager (CSV file I/O)
  ui/                            MainFrame, HeaderPanel, SearchBookPanel, BookingDialog,
                                  PaymentDialog, ReceiptDialog, ManageReservationsPanel,
                                  RoomManagementPanel, table models, HotelLogo, UITheme
```

## How to Run

### Option 1 — Run the prebuilt JAR (easiest)
Requires Java 17+ installed.
```
java -jar GrandStay-Hotel-Reservation.jar
```
A `data/` folder will be created next to the jar the first time you run it, holding `rooms.csv` and `reservations.csv`.

### Option 2 — Build from source
```
cd GrandStay-HotelReservationSystem
javac -d out -encoding UTF-8 $(find src -name "*.java")
java -cp out com.hotel.Main
```

### Option 3 — Open in an IDE
Import the `src` folder as a Java project in IntelliJ IDEA, Eclipse, or NetBeans, set `com.hotel.Main` as the run configuration's main class, and run.

## Notes
- The app seeds 12 demo rooms (mix of Standard/Deluxe/Suite across 3 floors) the very first time it runs, if `data/rooms.csv` is empty.
- Payment is fully simulated — no real gateway or network call is made. Entering a card number ending in `0000` will simulate a declined payment, useful for demoing the failure path.
- Deleting the `data/` folder resets the system back to the seeded demo state.
