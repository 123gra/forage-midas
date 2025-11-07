
# Project Overview

**Midas Core** is a production-grade transaction processing system designed to handle real-time financial transactions with validation, database persistence, incentive integration, and balance querying capabilities. This project demonstrates core concepts in microservices architecture, Spring Framework, and financial software design.

### Key Technologies

- **Backend**: Spring Boot 3.2.5 (Java 17)
- **Messaging**: Apache Kafka
- **Database**: H2 (In-memory for development)
- **ORM**: JPA/Hibernate
- **REST API**: Spring Web
- **Build**: Maven

---

## Tasks Completed

### **Task 1-2: Project Setup & Kafka Integration**

Established a Spring Boot application with Kafka consumer integration to receive and deserialize real-time transaction messages from a Kafka topic.

**Achievements**:

- Configured embedded Kafka for local development
- Implemented `TransactionListener` component with `@KafkaListener` annotation
- Built JSON deserialization pipeline for incoming transaction data
- Created supporting test utilities (KafkaProducer, FileLoader, UserPopulator)

**Output**: Successfully receives and deserializes transaction stream with transaction amount tracking

---

### **Task 3: Database Integration & Transaction Validation**

Implemented H2 database integration with JPA/Hibernate and built comprehensive transaction validation logic with atomic balance updates.

**Achievements**:

- Designed `UserRecord` entity with balance management
- Created `TransactionRecord` entity with many-to-one relationships to sender/recipient users
- Implemented three-tier validation:
  - ✅ Sender exists in database
  - ✅ Recipient exists in database
  - ✅ Sender has sufficient balance for transaction
- Built atomic transaction processing with balance adjustments
- Configured H2 database with auto-schema generation

**Key Features**:

- **Valid Transactions**: Recorded to database with sender balance reduced and recipient balance increased
- **Invalid Transactions**: Safely discarded without any database modifications
- **Data Integrity**: Foreign key relationships ensure referential integrity

**Test Result**: Transaction validated with balance verification

---

### **Task 4: Incentive API Integration**

Integrated a third-party REST API for calculating dynamic transaction incentives, creating a clean separation between core transaction logic and incentive calculation.

**Achievements**:

- Created `Incentive` class for API response deserialization
- Implemented `RestTemplate` bean for HTTP communication
- Built incentive fetching pipeline:
  - POST transaction to incentive API after validation
  - Receive calculated incentive amount
  - Store incentive with transaction record
- Updated balance calculation logic:
  - **Sender**: Deducted only the transaction amount
  - **Recipient**: Credited with transaction amount + incentive (bonus rewards)
- Added error handling for API failures with graceful degradation

**Key Features**:

- Asynchronous incentive calculation without blocking transaction processing
- Clean separation between Midas Core and incentive service
- Extensible architecture for future modifications

**Test Result**: Successfully processed 22 transactions with incentive calculations

---

### **Task 5: Balance Query REST API**

Exposed a production-grade REST API endpoint for users to query their real-time account balances, enabling customer-facing balance inquiries.

**Achievements**:

- Created `BalanceController` with `/balance` GET endpoint
- Configured application to run on port 33400
- Implemented balance retrieval with graceful handling:
  - Returns user balance if exists
  - Returns 0 if user doesn't exist
- Integrated with existing UserRepository for database queries
- Added comprehensive error handling

**API Specification**:

```
GET /balance?userId={userId}
Content-Type: application/json

Response:
{
  "amount": 2567.52
}
```

**Test Result**: Successfully queried all 13 users and returned accurate balances with proper JSON serialization

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Midas Core Application                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐         ┌──────────────────┐          │
│  │  Kafka Listener  │         │  Balance REST    │          │
│  │  (Port 9092)     │         │  Controller      │          │
│  │                  │         │  (Port 33400)    │          │
│  └────────┬─────────┘         └────────┬─────────┘          │
│           │                            │                    │
│           ├─────────────────────────────┤                   │
│           ▼                             ▼                   │
│  ┌──────────────────────────────────────────────┐           │
│  │    Transaction Validation & Persistence     │            │
│  │  ┌─────────────────────────────────────┐   │             │
│  │  │ • Sender/Recipient Validation       │   │             │
│  │  │ • Balance Sufficiency Check         │   │             │
│  │  │ • Atomic Transaction Processing     │   │             │
│  │  └─────────────────────────────────────┘   │             │
│  └────────────┬─────────────────────────────────┘           │
│               │                                             │
│               ├─────────────────┐                           │
│               ▼                 ▼                           │
│  ┌─────────────────────┐  ┌───────────────────┐             │
│  │  H2 Database (JPA)  │  │ Incentive API     │             │
│  │                     │  │ Integration       │             │
│  │ • UserRecord        │  │                   │             │
│  │ • TransactionRecord │  │ (REST Client)     │             │
│  │                     │  │ (Port 8080)       │             │
│  └─────────────────────┘  └───────────────────┘             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
jpmc-midas-project/
├── src/
│   ├── main/java/com/jpmc/midascore/
│   │   ├── MidasCoreApplication.java
│   │   ├── component/
│   │   │   └── DatabaseConduit.java
│   │   ├── controller/
│   │   │   └── BalanceController.java          # Task 5
│   │   ├── entity/
│   │   │   ├── UserRecord.java
│   │   │   └── TransactionRecord.java          # Task 3
│   │   ├── foundation/
│   │   │   ├── Transaction.java
│   │   │   ├── Incentive.java                  # Task 4
│   │   │   └── Balance.java                    # Task 5
│   │   ├── kafka/
│   │   │   ├── KafkaConfig.java
│   │   │   └── TransactionListener.java        # Tasks 1-5
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── TransactionRepository.java      # Task 3
│   │   └── config/
│   │       └── AppConfig.java                  # Task 4
│   └── test/java/com/jpmc/midascore/
│       ├── TaskThreeTests.java
│       ├── TaskFourTests.java
│       ├── TaskFiveTests.java
│       └── [Test utilities]
├── application.yml                              # Configuration
├── pom.xml                                      # Dependencies
└── README.md
```