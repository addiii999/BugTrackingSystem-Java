# Bug Tracking System

A structured and practical Bug Tracking System built in Java using clean architecture principles.
This project demonstrates how real-world issue management systems are designed and implemented at a foundational level.

It is not just a console application. It is a structured system that reflects how software teams track, assign, update, and manage issues in real development environments.

---

## Overview

This application allows users to:

* Create bugs with priority levels
* View all reported bugs
* Assign bugs to developers
* Change bug status (Open, In Progress, Closed)
* Delete bugs
* Search bugs by ID or Title
* Persist data using file storage

The system follows a layered structure separating data models from business logic, creating a clean and scalable design.

---

## Project Structure

```
BugTrackingSystem/
 ├── src/
 │     ├── model/
 │     │     └── Bug.java
 │     ├── service/
 │     │     └── BugService.java
 │     └── Main.java
 ├── bugs.txt
 ├── .gitignore
 └── README.md
```

### Architecture Design

* **Model Layer** – Defines the Bug entity.
* **Service Layer** – Handles all business logic and file operations.
* **Main Layer** – Manages user interaction and program flow.

This separation ensures clarity, maintainability, and scalability.

---

## Key Features

* Clean object-oriented implementation
* Proper separation of concerns
* File-based data persistence
* Robust error handling
* Full CRUD operations
* Search functionality
* Structured console interface

---

## Technologies Used

* Java
* Object-Oriented Programming
* File Handling
* Layered Architecture Design

---

## How to Run

Open terminal inside the project directory and execute:

```
cd src
javac model/Bug.java service/BugService.java Main.java
java Main
```

---

## Why This Project Matters

This project simulates a simplified version of systems used in software companies to manage development issues.
It reflects understanding of:

* System design fundamentals
* Data management
* Code structuring
* Practical application of OOP

It is designed to move beyond basic academic projects and demonstrate structured thinking and clean implementation.