# Midas
Project repo for the JPMC Advanced Software Engineering Forage program

Completed repo for reference - https://github.com/vagabond-systems/forage-midas-complete.git

## Features

- **Transaction Processing**: Validates and processes financial transactions via Kafka
- **Database Integration**: H2 in-memory database with JPA/Hibernate
- **Incentive System**: Integrates with external incentives API for transaction rewards
- **REST API**: Exposes `/balance` endpoint for querying user balances
- **Real-time Processing**: Kafka consumer for real-time transaction handling

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Kafka 3.1.4
- H2 Database
- JPA/Hibernate
- Maven

## Quick Start

### Using IntelliJ IDEA

1. **Start Incentives API**:
   - Open terminal in IntelliJ (View → Tool Windows → Terminal)
   - Run: `java -jar services/transaction-incentive-api.jar`
   - Keep this running in the terminal

2. **Run Application**:
   - Right-click on `MidasCoreApplication.java`
   - Select "Run 'MidasCoreApplication'"
   - Or use the green play button next to the main method

3. **Run Tests**:
   - Right-click on any test class (e.g., `TaskOneTests.java`)
   - Select "Run 'TaskOneTests'"
   - For debugging: Select "Debug 'TaskOneTests'"

4. **Access H2 Console**: http://localhost:33400/h2-console

### Using Command Line

1. **Start Incentives API**:
   ```bash
   java -jar services/transaction-incentive-api.jar
   ```

2. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Access H2 Console**: http://localhost:33400/h2-console

## API Endpoints

- `GET /balance?userId={id}` - Get user balance (runs on port 33400)