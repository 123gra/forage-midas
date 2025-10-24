# Midas Core - Financial Transaction System
A Spring Boot application for processing financial transactions with Kafka messaging and database integration.

🚀 Quick Start
bash
# Clone repository
git clone https://github.com/your-username/forage-midas.git
cd forage-midas

# Build project
./mvnw clean compile

# Run tests
./mvnw test

# Run application
./mvnw spring-boot:run
🛠️ Tech Stack
Java 17 - Core programming language

Spring Boot 3.2.5 - Application framework

Apache Kafka - Message queue for transactions

H2 Database - In-memory SQL database

Spring Data JPA - Database abstraction layer

Maven - Build and dependency management

📋 Features
✅ Kafka Integration - Asynchronous transaction processing

✅ Transaction Validation - Balance checks and user verification

✅ Database Storage - Persistent transaction records

✅ REST API - HTTP endpoints for system interaction

✅ Comprehensive Testing - Unit and integration tests

🏗️ Architecture
text
Transaction → Kafka → Validator → Database
    ↑              ↓
 Frontend     Balance Updates
🧪 Testing
bash
# Run all tests
./mvnw test

# Run specific task tests
./mvnw test -Dtest=TaskOneTests    # Dependencies & setup
./mvnw test -Dtest=TaskTwoTests    # Kafka integration  
./mvnw test -Dtest=TaskThreeTests  # Database integration
📁 Core Components
MidasCoreApplication - Main Spring Boot application

DatabaseConduit - Transaction processing logic

TransactionListener - Kafka message consumer

User/TransactionRecord - JPA entities

Repository Interfaces - Data access layer

# ⚙️ Configuration
Key configuration in application.yml:

Kafka bootstrap servers and consumer groups

H2 database connection

JPA settings for entity management

# 🎯 Project Purpose
Built as part of the JPMC Forage internship program to demonstrate:

Microservices architecture

Message-driven systems

Database integration

Financial transaction processing
