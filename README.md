# Smart Hotel Reservation and Management System

A desktop hotel reservation and management system built with **JavaFX**, **Maven**, and **SQLite**. The system provides separate portals for customers and administrators, featuring dynamic room reservations, customizable extra services (Laundry, Gym, Swimming Pool, Food), service request approval workflows, room image galleries, and review/rating management.

---

## Table of Contents
1. [Key Features](#key-features)
2. [Architecture & Tech Stack](#architecture--tech-stack)
3. [Default Credentials](#default-credentials)
4. [Prerequisites & Getting Started](#prerequisites--getting-started)
5. [Git & Version Control Guide](#git--version-control-guide)
   - [Version Control Basics](#version-control-basics)
   - [Git Configuration](#git-configuration)
   - [Repository Operations](#repository-operations)
   - [Branching & Merging](#branching--merging)
   - [GitHub & Remote Workflows](#github--remote-workflows)
6. [Project Structure](#project-structure)

---

## Key Features

### Customer Portal
- **Room Browsing & Booking:** Browse available rooms, select stay dates, view dynamic price calculation, and view room photo previews.
- **Service Requests:** Request ancillary services (Laundry, Swimming Pool, Gym, Food). Service charges are automatically computed upon admin approval.
- **Reservation Tracking:** Real-time visibility into booking status, service approvals, and administrative messages.
- **Ratings & Reviews:** Submit verified ratings (1–5 stars) and feedback for completed reservations.

### Admin Portal
- **Room Management:** Add new rooms, update pricing, update room details, and upload room photography.
- **Service Order Approvals:** Review customer service requests with the ability to `APPROVE` or `REJECT` alongside feedback notes.
- **Reservation Management:** Monitor check-ins, check-outs, and booking bills.
- **Customer Feedback Dashboard:** Centralized view of customer reviews and ratings.

---

## Architecture & Tech Stack
- **Language & Runtime:** Java (JDK 21+)
- **UI Framework:** OpenJFX (JavaFX Controls & FXML)
- **Database:** SQLite (Embedded relational storage via `sqlite-jdbc`)
- **JSON Serialization:** Jackson Databind
- **Build System:** Apache Maven

---

## Default Credentials

| Role | Username | Password |
|---|---|---|
| **Admin** | `pratik` | `pratik123` |

*Note: New customers can register directly through the application's Registration screen.*

---

## Prerequisites & Getting Started

1. **JDK 21 or higher** installed and configured in your `PATH`.
2. **Maven 3.8+** installed (or use IntelliJ's bundled Maven).
3. Clone or open the project in **IntelliJ IDEA**:
   ```bash
   mvn clean compile javafx:run
   ```
   Or run the main class `com.smarthotel.Main`.

---

## Git & Version Control Guide

### Version Control Basics
A **Version Control System (VCS)** is a software tool that tracks changes to source code over time. Key benefits include:
- **Collaboration:** Enables multiple developers to work concurrently on the same codebase without overwriting changes.
- **History Tracking:** Maintains a complete audit log of every modification, author, timestamp, and purpose.
- **Rollback:** Allows reverting breaking changes to a previous stable state at any point.
- **Branching & Merging:** Facilitates isolated feature development and experimentation before merging into production.
- **Backup & Distributed Redundancy:** Every local clone contains the full project history.

### Git Configuration
Configure your identity and default branch settings:
```bash
# Set your name and email
git config --global user.name "Pratik Jha"
git config --global user.email "jha2307122@stud.kuet.ac.bd"

# Set default branch name
git config --global init.defaultBranch master

# Inspect configuration
git config --list
```

### Repository Operations
```bash
# Initialize a new local repository
git init

# Check working tree status
git status

# Inspect differences
git diff

# Stage files
git add <filename>
git add .

# Unstage files
git restore --staged <filename>
git rm --cached <filename>

# Commit changes
git commit -m "feat: descriptive commit message"
git commit -a -m "feat: commit tracked modified files"

# Amend the last commit
git commit --amend -m "chore: updated commit message"
```

### Branching & Merging
```bash
# List branches
git branch

# Create and switch to a feature branch
git switch -c feature/new-module
# or: git checkout -b feature/new-module

# Switch back to master
git switch master

# Merge feature branch into master
git merge feature/new-module
```

### GitHub & Remote Workflows
```bash
# Connect local repository to GitHub
git remote add origin https://github.com/<username>/<repository-name>.git

# Push changes and set upstream tracking
git push -u origin master

# Pull updates from remote
git pull origin master
```

---

## Project Structure
```text
Smart Hotel Reservation and Management System/
├── .gitignore
├── README.md
└── SmartHotelReservation_Updated/
    └── SmartHotelReservation/
        ├── pom.xml
        ├── README.md
        ├── room_images/
        └── src/
            └── main/
                ├── java/com/smarthotel/
                │   ├── Main.java
                │   ├── config/AppConfig.java
                │   ├── controller/ (Admin, Customer, Login, Register, SessionAware)
                │   ├── dao/        (User, Customer, Room, Reservation, Payment, Service, Review)
                │   ├── database/   (Database.java SQLite Connection & Migration)
                │   ├── model/      (Person, User, Customer, Room, Reservation, Payable, Bill, etc.)
                │   ├── network/    (HotelApiService.java)
                │   ├── service/    (AuthService, HotelService)
                │   └── util/       (PasswordUtil, Async)
                └── resources/
                    ├── css/app.css
                    └── fxml/       (admin.fxml, customer.fxml, login.fxml, register.fxml)
```
