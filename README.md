# 🏦 JPMorgan Chase Forage Virtual Internship – Midas Core

This repository contains my completed solution for the **Midas Core** component as part of the **JPMorgan Chase Software Engineering Virtual Experience** on [Forage](https://www.theforage.com/virtual-internships/prototype/jpmorgan-chase/software-engineering).

The project simulates a production-grade financial processing system that integrates **Kafka**, **Spring Boot**, **H2 Database**, and a **REST Incentive API**, all designed to mimic a real-world microservices environment.

---

## 🚧 Project Overview

**Midas Core** is the central component responsible for:
- Receiving real-time transaction events via Kafka
- Validating and storing financial transactions in a SQL database
- Applying incentives from an external REST API
- Exposing user balances through a REST endpoint

---

## ✅ Tasks Completed

### 🔹 Task 1: Project Setup & Dependency Management
- Forked and cloned the scaffolded repo: [`vagabond-systems/forage-midas`](https://github.com/vagabond-systems/forage-midas)
- Configured the project in IntelliJ with Java 17 and Maven
- Added required dependencies to `pom.xml`:
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-web`
  - `spring-kafka`
  - `h2`
  - `spring-boot-starter-test`
  - `spring-kafka-test`
  - `testcontainers-kafka`
- Verified setup by running `TaskOneTests`

---

### 🔹 Task 2: Kafka Integration
- Implemented a Kafka listener that:
  - Listens to the topic defined in `application.yml`
  - Deserializes incoming messages to `Transaction` objects
- Verified functionality using in-memory Kafka provided in the test suite
- Ran `TaskTwoTests` and used debugger to inspect message handling

---

### 🔹 Task 3: H2 Database Integration & Validation Logic
- Created `TransactionRecord` entity to persist valid transactions
- Implemented validation rules:
  - Valid sender and recipient IDs
  - Sufficient sender balance
- Updated balances upon successful transaction processing
- Ensured one-to-many relationships between `User` and `TransactionRecord`
- Ran `TaskThreeTests` and verified post-processing balances

---

### 🔹 Task 4: REST Incentive API Integration
- Integrated external API using `RestTemplate` to post validated transactions
- Received incentive amounts via `/incentive` endpoint
- Stored incentives alongside transactions
- Adjusted recipient balances to include incentive amounts
- Ensured incentives were not deducted from sender balances
- Ran `TaskFourTests` and verified updated balances

---

### 🔹 Task 5: User Balance REST API
- Developed a REST controller exposing a `GET /balance` endpoint
- Accepts `userId` as a query parameter
- Returns JSON response using the provided `Balance` class
- Default response for unknown users: balance of 0
- Exposed service on **port 33400**
- Confirmed coexistence of Kafka listener and REST controller
- Ran `TaskFiveTests` for final verification

---

## 🧪 Tech Stack

| Tech          | Description                            |
|---------------|----------------------------------------|
| Java 17       | Core programming language              |
| Spring Boot   | Application framework                  |
| Kafka         | Asynchronous message broker            |
| H2            | In-memory SQL database                 |
| REST API      | External incentive service integration |
| Maven         | Dependency and build management        |
| JUnit         | Testing framework                      |

---

## 📂 Project Structure
```text
src/
├── main/
│   └── java/
│       └── com/example/midascore/
│           ├── MidasCoreApplication.java
│           ├── config/
│           ├── controller/
│           ├── kafka/
│           ├── model/
│           ├── repository/
│           ├── service/
├── test/
│   └── java/
│       └── ... (TaskOneTests through TaskFiveTests)
```

---

## 🚀 How to Run

1. Clone the repo and navigate into it.
2. Start the Incentive API (`services/incentive-api.jar`) on port `8080`.
3. Run the Spring Boot application on port `33400`.
4. Use Postman or your browser to hit:

GET http://localhost:33400/balance?userId=someUser

---

## 📜 License & Credits

- This project is based on the [JPMorgan Chase Forage Internship](https://www.theforage.com/virtual-internships/prototype/jpmorgan-chase/software-engineering)
- Educational and non-commercial use only
- Original repo: [vagabond-systems/forage-midas](https://github.com/vagabond-systems/forage-midas)

