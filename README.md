# Midas

Project repository for the JPMC Advanced Software Engineering Forage program.

---

## 📌 Original Project
This repository is a fork of the official Midas Core project used in the JPMC Forage program.  
It contains Kafka-based transaction processing, integration with Incentive API, and a REST endpoint for querying user balances.

---

## 📌 My Notes
These are my personal notes and observations while working on the project:

### 1. Tasks Implemented
- Task 1: Repository setup and initial testing.
- Task 2: Kafka listener integration and message handling.
- Task 3: H2 in-memory database integration and JPA persistence.
- Task 4: Incentive API integration via `RestTemplate`.
- Task 5: REST endpoint (`/balance`) with layered architecture:
  - **Controller → Service → Repository**

### 2. Implementation Notes
- Used **Lombok** cautiously on `UserRecord` and `TransactionRecord` to avoid persistence issues.
- Maintained separation of concerns and layered structure.
- Tested Kafka listener, database operations, and REST endpoint thoroughly.
- Updated `application.yml` for Kafka, H2, server port, and API configs.

---

### Testing Notes
- Started the `services` `->` `transaction-incentive-api.jar` service so tests could call the external endpoint via `RestTemplate`.
- Used a debugger after `Thread.sleep()` in TaskThreeTests/TaskFourTests and evaluated `this.userPopulator.databaseConduit.userRepository.findAll()` to check Wilbur’s final balance.
