# Midas Core - Transaction Processing System

![Completion-date](https://img.shields.io/badge/Completed-November%202nd%2C%202025-success)

![Certificate](https://raw.githubusercontent.com/likhith-ts/forage-midas/refs/heads/solution/images/jpmc-forage-sde_page-0001.jpg)

## 🎓 Program Completion

I successfully completed **J.P. Morgan Chase & Co.'s Software Engineering Job Simulation** on Forage, earning a Certificate of Completion on November 2nd, 2025.

### Certificate Details

- **Program**: Software Engineering Job Simulation
- **Organization**: JP Morgan Chase & Co.
- **Platform**: Forage
- **Date Completed**: November 2nd, 2025
- **Verification Code**: 68Bfe201d870d460da8e2e51e3

---

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8.9+
- Port 33400 available (REST API)
- Port 8080 available (Incentive API) - for Task 4+

### Setup

```bash
# Clone repository
git clone https://github.com/likhith-ts/forage-midas jpmc-midas-project
cd jpmc-midas-project

# Build project
./mvnw clean compile

# Start incentive API (required for full functionality)
java -jar services/transaction-incentive-api.jar &

# Run all tests
./mvnw test

# Run specific task tests
./mvnw test -Dtest=TaskOneTests
./mvnw test -Dtest=TaskTwoTests
./mvnw test -Dtest=TaskThreeTests
./mvnw test -Dtest=TaskFourTests
./mvnw test -Dtest=TaskFiveTests
```

---

## Test Results

| Task | Component             | Status  | Key Metrics                   |
| ---- | --------------------- | ------- | ----------------------------- |
| 1-2  | Kafka Integration     | ✅ PASS | 4 transactions deserialized   |
| 3    | Database & Validation | ✅ PASS | Waldorf balance: 10.42        |
| 4    | Incentive API         | ✅ PASS | Wilbur balance: 3089          |
| 5    | Balance REST API      | ✅ PASS | 13 users queried successfully |

---

## References & Resources

- [Project Overview](https://github.com/likhith-ts/forage-midas/blob/solution/Overview.md)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation)
- [JPA/Hibernate Guide](https://hibernate.org/orm/documentation)
- [H2 Database](https://h2database.com)
- [Forage Platform](https://www.theforage.com)

---

## License

This project is part of the @[Forage](https://www.theforage.com/) J.P. Morgan Chase Software Engineering program and is provided for educational purposes.
