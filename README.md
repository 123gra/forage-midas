# Midas

Project repository for the J.P. Morgan Advanced Software Engineering Forage program.

## Overview

Midas is a sample application that demonstrates transaction processing, incentive calculation, and RESTful API design using modern Java and Spring Boot. It simulates a simplified financial system with event-driven architecture and in-memory persistence.

## Features

- Transaction processing via Kafka topics
- Incentive calculation and application
- RESTful APIs for querying balances and incentives
- In-memory H2 database for development and testing
- Sample data and comprehensive unit tests

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Build and Run

```sh
./mvnw clean install
./mvnw spring-boot:run
```

The application will start at [http://localhost:33400](http://localhost:33400).

### Running Tests

```sh
./mvnw test
```

## Project Structure

- `src/main/java/` - Main application source code
- `src/test/java/` - Unit and integration tests
- `src/test/resources/` - Test data and configuration

## API Endpoints

- `GET /balance?userId={id}` - Retrieve user balance
- `GET /incentives/{userId}/amount` - Retrieve total incentives for a user

## License

This project is for educational purposes as part of the Forage program.

---