# Midas
Project repo for the JPMC Advanced Software Engineering Forage program

# Midas Core System

## Overview

**Midas Core** is a key component of the larger **Midas System**, a high-profile fintech platform designed to handle financial transactions with speed, reliability, and integrity. This project is part of a distributed system that integrates real-time transaction processing, data validation, and external API incentivization.

The goal of Midas Core is to **receive**, **validate**, and **record** incoming financial transactions. It leverages modern backend technologies such as **Spring Boot**, **Apache Kafka**, **SQL databases**, and **REST APIs** to ensure secure, scalable, and efficient processing.

---

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Apache Kafka** – for receiving streaming financial transaction data
- **PostgreSQL / MySQL** – for transaction validation and persistence
- **External REST APIs** – for handling transaction incentivization
- **Maven / Gradle** – for build and dependency management

---

## Architecture

Midas Core operates as a service within a microservices architecture:

1. **Receives transactions** via Kafka topics.
2. **Validates** each transaction against business rules and database records.
3. **Persists** the transaction data to a SQL database.
4. **Communicates** with an external REST API to trigger incentive mechanisms.

All components are wired using **Spring Boot’s dependency injection**, allowing clean modular development and easier testing.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven or Gradle
- Docker (optional, for local Kafka or DB setup)
- PostgreSQL or MySQL running locally or via Docker

### Setup

```bash
# Clone the repository
git clone https://github.com/YOUR-USERNAME/midas-core.git
cd midas-core

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
