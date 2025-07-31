# JPMorgan Chase Midas Analytics Service

## Overview

This repository contains the codebase for the Midas Analytics Service, a project designed to demonstrate modern microservice architecture using Java Spring Boot, with planned extensions in Go, Cassandra, and Terraform. The project is structured to provide a robust backend and, in the future, a Go-based analytics microservice and infrastructure as code.

---

## Project Structure

```
jpmc-Midas/
  forage-midas/
    go-analytics/
      main.go           # (Go microservice - planned, not yet functional)
      # (other Go files: e.g., consumer.go, database/, kafka/)
    src/
      main/
        java/
          com/jpmc/midascore/
            # Java Spring Boot backend (implemented)
    terraform/          # (Infrastructure as code - planned)
    README.md
    # ...other files
  go.mod
```

---

## Currently Implemented

### 1. **Java Spring Boot Backend (`forage-midas/src/main/java/com/jpmc/midascore/`)**

- **Entities:** `TransactionRecord`, `UserRecord`, etc.
- **Controllers:** `BalanceController`
- **Repositories:** `TransactionRepository`, `UserRepository`
- **Services:** `TransactionService`
- **Kafka Integration:** `TransactionListener`
- **Database Integration:** `DatabaseConduit`
- **Testing:** Multiple test classes for core functionality.

> **Note:** The Go microservice and Terraform infrastructure are not yet functional. Current Go code is experimental and not production-ready.

---

## Planned Additions

### 1. **Go Analytics Microservice (Planned)**

- **Kafka Producer & Consumer:**
  - Implement robust Kafka producer and consumer in Go to process and analyze messages.
- **Cassandra Integration:**
  - Use a Go Cassandra driver (e.g., `gocql`) to persist and query analytics data.
- **RESTful API:**
  - Expose analytics endpoints via HTTP using `gorilla/mux`.

### 2. **Infrastructure as Code (Terraform) (Planned)**

- **Automated Provisioning:**
  - Use Terraform to provision Kafka, Cassandra, and networking resources.
- **Environment Management:**
  - Separate configurations for development, staging, and production.

### 3. **CI/CD Integration (Planned)**

- Automated testing and deployment for both Java and Go services.
- Terraform plan/apply automation.

### 4. **Documentation and Developer Experience (Planned)**

- Comprehensive API documentation (Swagger/OpenAPI).
- Sample data and usage examples.
- Learning guides for running and extending the system.

---

## Getting Started


### Quick Start (Java Spring Boot Backend)

1. **Navigate to the project directory:**
   ```sh
   cd forage-midas
   ```
2. **Build and run the Spring Boot application:**
   ```sh
   ./mvnw spring-boot:run
   ```
3. **Run tests:**
   ```sh
   ./mvnw test
   ```

> **Note:** Go microservice and Terraform scripts are not yet functional. Instructions will be updated as these components are implemented.

---
