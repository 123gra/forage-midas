# JPMorgan Chase & Co – Midas Core Virtual Internship Project
This repository contains the implementation completed as part of the JPMorgan Chase & Co Software Engineering Virtual Experience (Forage).
The project, titled Midas Core, simulates a financial transaction processing system that handles real-time transactions, validates users and balances, integrates with an external Incentive API, and provides REST endpoints for user balance retrieval.

The system was built using Spring Boot and Kafka, with data persistence managed through an in-memory H2 database. Each task in this project represents a progressive development milestone in building a complete financial backend system.

Tech Stack:
Programming Language - Java 17
Framework	- Spring Boot
Database - H2 In-Memory Database
Messaging Queue	- Apache Kafka
Build Tool - Maven
REST Communication - Spring RestTemplate
Testing - JUnit, Maven Surefire Plugin
API Format - JSON

Project Structure and Tasks:

Task 1: Kafka Consumer Integration
Configured a Kafka consumer within Midas Core to listen to transaction events.
Implemented logic to deserialize and process incoming transaction data.
Verified that Kafka successfully streams transaction messages into the system.
Outcome:
Kafka listener successfully receives and processes transaction messages.

Task 2: Transaction Validation
Implemented validation logic to ensure only legitimate transactions are processed.
Checked sender and recipient existence and ensured sufficient account balance.
Updated balances accordingly after each valid transaction.
Outcome:
System now validates each transaction and maintains accurate account balances.

Task 3: Database Persistence (H2 Integration)
Integrated an H2 database to store user and transaction records.
Created JPA entities: UserRecord and TransactionRecord.
Added persistence layer using DatabaseConduit and UserPopulator.
Modified TransactionService to update and store all transaction data.
Outcome:
Transactions are stored in an H2 database and persist during runtime.
Verified correct user balances after transaction execution.

Task 4: Incentive API Integration
Integrated an external Incentive API (/incentive) using Spring’s RestTemplate.
Implemented a new class, IncentiveService, to handle REST communication.
Updated TransactionService to call the Incentive API after each transaction.
Added logic to apply incentive amounts to recipient balances.
Validated functionality using TaskFourTests.
Outcome:
System integrates incentive calculation successfully.
All user balances now include incentives (final Wilbur balance verified as 3321.84).

Task 5: REST API for User Balance Retrieval
Developed a REST endpoint /balance to allow users to query their balance using userId.
Implemented BalanceController to handle GET requests.
Configured application to run on port 33400.
Returned a balance of 0.0 for non-existent users.
Verified the output using TaskFiveTests after running the Incentive API service.
Outcome:
Users can retrieve their current balances (including incentives) via REST API.

System Architecture:
Kafka Topic  →  Midas Core (Kafka Listener)
               ↓
        Transaction Validation
               ↓
          H2 Database Storage
               ↓
      External Incentive API (REST)
               ↓
      Updated Balances in Database
               ↓
     REST Endpoint: /balance → JSON Response

Final Outcome:
Built a fully functional backend simulation for processing and tracking financial transactions.
Integrated Kafka for real-time transaction processing.
Added incentive calculation via REST API.
Developed a REST controller for balance queries.
Successfully completed all tasks and passed all tests.
