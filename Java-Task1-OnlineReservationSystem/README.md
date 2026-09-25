# Online Reservation System

## Project Overview

The Online Reservation System is a GUI-based train reservation application developed using Java Swing and SQLite JDBC.

The system allows users to log in, book train tickets, view reservation details using a PNR number, and cancel reservations.

## Technologies Used

- Java
- Java Swing
- JDBC
- SQLite
- SQLite JDBC Driver

## Features

- User login with username and password
- Invalid login validation
- Train number validation
- Automatic display of train details
- Passenger reservation form
- Class type selection
- Date of journey validation
- Automatic unique PNR generation
- Booking confirmation dialog
- Reservation details retrieval using PNR
- Ticket cancellation with confirmation
- Basic input validation

## Project Structure

```text
Java-Task1-OnlineReservationSystem/
├── src/
│   ├── Main.java
│   ├── LoginFrame.java
│   ├── ReservationFrame.java
│   ├── Database.java
│   └── Reservation.java
├── lib/
│   └── sqlite-jdbc-3.53.4.0.jar
├── screenshots/
└── README.md