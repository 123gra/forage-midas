# Midas Core

Project repo for the JPMC Advanced Software Engineering Virtual Experience program.

## Overview

This is a Spring Boot application that demonstrates various software engineering concepts including:
- Spring Boot with JPA and Web starters
- H2 in-memory database for development
- Kafka integration for messaging
- Comprehensive test suite with Testcontainers

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **Spring Web**
- **Spring Kafka 3.1.4**
- **H2 Database 2.2.224**
- **Testcontainers 1.19.1**
- **JUnit 5**

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running Tests

Run the test suite:
```bash
./mvnw test
```

Run a specific test:
```bash
./mvnw test -Dtest=TaskOneTests
```

## Configuration

The application uses H2 in-memory database with the following configuration:
- Database URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

JPA settings:
- DDL Auto: `create-drop`
- SQL Logging: enabled
- Dialect: H2

## Project Structure

```
src/
├── main/
│   ├── java/com/jpmc/midascore/
│   │   ├── component/     # Spring components
│   │   ├── entity/        # JPA entities
│   │   ├── foundation/    # Core business models
│   │   └── repository/    # Data repositories
│   └── resources/
│       ├── application.yml    # Application configuration
│       └── logback-spring.xml # Logging configuration
└── test/
    ├── java/com/jpmc/midascore/  # Test classes
    └── resources/test_data/      # Test data files
```

## Tasks

The project includes multiple tasks (TaskOneTests through TaskFiveTests) that demonstrate different aspects of the application functionality.
