# 🏦 J.P. Morgan Software Engineering Virtual Experience Program (via Forage)

This repository contains my completed work for the **JPMorgan Chase & Co. Software Engineering Virtual Experience** hosted on [Forage](https://www.theforage.com/). The program simulates real-world backend engineering tasks that developers at JPMC work on daily, focusing on building robust systems that process financial transactions in real time.

---

## 📌 Project Summary

The core goal of this project was to build a **Spring Boot-based backend service** that:
- Listens to incoming transaction data from **Kafka**
- Validates and stores transactions using **JPA and an H2 SQL database**
- Communicates with an external **Incentives API** via REST
- Exposes user **balance data** through a secure `/balance` REST endpoint

---

## ✅ Tasks Completed

### 🔹 Task 1: Project Setup
Initialized the project and verified the Spring Boot + Kafka pipeline.

### 🔹 Task 2: Kafka Producer-Consumer Integration
Implemented a Kafka producer that sends transaction data and a consumer that listens and parses incoming transactions into Java objects.

### 🔹 Task 3: Database Integration
- Integrated an **H2 in-memory SQL database**
- Created `UserRecord` and `TransactionRecord` entities
- Validated transactions based on user existence and balance
- Adjusted sender and recipient balances accordingly

### 🔹 Task 4: Incentive API Integration
- Connected to a black-box REST API (`http://localhost:8080/incentive`)
- Retrieved incentives for each valid transaction
- Updated recipient balances based on incentive received

### 🔹 Task 5: Exposed Balance API
- Built a REST endpoint `/balance` that accepts a `userId` and returns the current balance
- Configured Spring Boot to run the service on port **33400**
- Final testing and verification using `TaskFiveTests`

---

## 🧰 Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Kafka**
- **Spring Data JPA**
- **H2 Database**
- **REST API (via RestTemplate)**
- **Maven**
- **JUnit 5**
- **Forage Virtual Experience Platform**

---

## 📈 Learning Outcomes

Through this experience, I gained hands-on experience in:

- Building microservices in **Spring Boot**
- Real-time messaging systems using **Kafka**
- Creating and managing SQL-based persistence with **JPA + H2**
- Integrating and consuming **external REST APIs**
- Debugging complex flows and writing scalable architecture
- Understanding how large organizations like **J.P. Morgan** build resilient backend systems

---

## 🎓 Certificate

I successfully completed this virtual experience and received a certificate from J.P. Morgan & Forage.

[Certificate](https://forage-uploads-prod.s3.amazonaws.com/completion-certificates/Sj7temL583QAYpHXD/E6McHJDKsQYh79moz_Sj7temL583QAYpHXD_yT6HbXRiwn8QK7mHm_1752513598679_completion_certificate.pdf)

---

## 🙏 Acknowledgements

Huge thanks to:
- **JPMorgan Chase & Co.** for designing such a realistic and engaging program
- **Forage** for making world-class learning accessible to students globally

---
---

## 🚀 How to Run

1. Clone the repository
2. Start the **incentive API**:
   ```bash
   java -jar services/transaction-incentive-api.jar
3. Run the Midas Core Application (SpringBootApplication)
4. Access:
   - **Kafka Consumer/Producer running in background**
   - **H2 console: http://localhost:33400/h2-console**
   - **Balance API: http://localhost:33400/balance?userId=1**
