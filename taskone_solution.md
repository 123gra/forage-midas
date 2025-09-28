# Task One Solution - Midas Core Application

## Summary
This document contains the solution and steps taken to complete Task One of the Midas Core application, including database setup, test execution, and verification. It also includes detailed instructions for accessing the H2 console and troubleshooting common issues.

## Key Steps Performed

### 1. Database Configuration
- Configured H2 in-memory database in `application.yml`
- Enabled H2 console for database inspection
- Set up JPA/Hibernate for database operations

### 2. Test Execution
- Successfully ran `TaskOneTests` using Maven
- Verified database operations and application startup

### 3. Test Output
The test produced the following output:

```
---begin output ---
1142725631254665682354316777216387420489
---end output ---
```

## Configuration Details

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
    driver-class-name: org.h2.Driver
    hikari:
      connection-timeout: 30000
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
      initialization-fail-timeout: 1

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        '[format_sql]': true
        '[order_inserts]': true
        '[order_updates]': true
        '[batch_versioned_data]': true
        jdbc:
          '[batch_size]': 50
          '[fetch_size]': 100

  h2:
    console:
      enabled: true
      path: /h2-console
      settings:
        web-allow-others: true
        trace: true
```

## Detailed Steps

### Accessing H2 Console
1. **Start the Spring Boot application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Access the H2 Console**:
   - Open your web browser
   - Go to: http://localhost:8080/h2-console
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **User Name**: `sa`
   - **Password**: (leave empty)
   - Click **Connect**

### Troubleshooting H2 Console Access
If you encounter issues:
- Clear your browser cache
- Try a different browser
- Ensure no other application is using port 8080
- Check application logs for errors during startup

### Running Tests
To execute the required test:
```bash
mvn test -Dtest=TaskOneTests
```

### Test Output Verification
Look for this section in the test output:
```
---begin output ---
1142725631254665682354316777216387420489
---end output---
```

### Key Changes Made
- Switched from Oracle to H2 in-memory database
- Enabled and configured the H2 console
- Fixed duplicate JPA configuration
- Added proper H2 connection parameters
- Cleaned up the configuration file

## Verification
- Successfully connected to H2 console at http://localhost:8080/h2-console
- Verified database schema and table creation
- Confirmed test output matches expected format
- Successfully executed `TaskOneTests` with expected output

## Dependencies Used
- Spring Boot Starter Data JPA: 3.2.5
- Spring Boot Starter Web: 3.2.5
- Spring Kafka: 3.1.4
- H2 Database: 2.2.224
- Spring Boot Starter Test: 3.2.5
- Spring Kafka Test: 3.1.4
- TestContainers Kafka: 1.19.1

## Next Steps
1. Submit the test output as required by your task
2. The unique code `1142725631254665682354316777216387420489` is your verification token
3. Make sure to include the `---begin output---` and `---end output---` lines when submitting
4. This output confirms your Spring Boot application is working correctly with the H2 database

## Database Operations (Optional)
You can perform the following operations in the H2 console:

### View Data
```sql
SELECT * FROM USER_RECORD;
```

### Insert Test Data
```sql
INSERT INTO USER_RECORD (id, name, balance) VALUES (1, 'Test User', 100.50);
```

### View Table Structure
```sql
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'USER_RECORD';
```
