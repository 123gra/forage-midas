# Midas Core - H2 Database Integration

This document describes the H2 database integration for the Midas Core application.

## Overview

Midas Core is now fully integrated with H2 database, providing persistent storage for users and transactions. The application supports both in-memory and file-based H2 database configurations.

## Database Configuration

### Current Setup (In-Memory)
- **Database URL**: `jdbc:h2:mem:midasdb`
- **Username**: `midas`
- **Password**: `midas123`
- **H2 Console**: Enabled at `/h2-console`

### Switching to File-Based Storage
To use persistent file-based storage, update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/midasdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    hibernate:
      ddl-auto: update  # Change from create-drop to update
```

## Database Schema

### Users Table (USER_RECORD)
- `ID` (BIGINT, Primary Key, Auto-generated)
- `NAME` (VARCHAR, Not Null)
- `BALANCE` (FLOAT, Not Null)

### Transactions Table (TRANSACTIONS)
- `ID` (BIGINT, Primary Key, Auto-generated)
- `SENDER_ID` (BIGINT, Not Null)
- `RECIPIENT_ID` (BIGINT, Not Null)
- `AMOUNT` (FLOAT, Not Null)
- `TIMESTAMP` (TIMESTAMP, Not Null)
- `STATUS` (VARCHAR, Not Null) - Values: PENDING, COMPLETED, FAILED, CANCELLED

## Sample Data

The application initializes with sample data (see `data.sql`):
- 5 sample users with initial balances
- 10 sample transactions with various statuses

## REST API Endpoints

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}/balance` - Update user balance
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/{id}/stats` - Get user transaction statistics

### Transaction Management
- `GET /api/transactions/{id}` - Get transaction by ID
- `GET /api/transactions/user/{userId}` - Get all transactions for a user
- `GET /api/transactions/status/{status}` - Get transactions by status
- `GET /api/transactions/recent` - Get recent transactions (last 24 hours)
- `POST /api/transactions/transfer` - Process money transfer
- `POST /api/transactions` - Create transaction record

## Usage Examples

### Starting the Application
```bash
mvn spring-boot:run
```

### Accessing H2 Console
1. Navigate to `http://localhost:8080/h2-console`
2. Use the following connection details:
   - **JDBC URL**: `jdbc:h2:mem:midasdb`
   - **Username**: `midas`
   - **Password**: `midas123`
3. Click "Connect"

### API Usage Examples

#### Create a new user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "initialBalance": 1000.0}'
```

#### Process a transfer:
```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -d '{"senderId": 1, "recipientId": 2, "amount": 100.0}'
```

#### Get user statistics:
```bash
curl http://localhost:8080/api/users/1/stats
```

## Key Features

1. **Full JPA Integration**: Entities are properly annotated with JPA annotations
2. **Transaction Management**: Automatic transaction processing with balance validation
3. **Comprehensive Repository Layer**: Rich query methods for data retrieval
4. **REST API**: Complete REST endpoints for all database operations
5. **H2 Console Access**: Easy database inspection and management
6. **Sample Data**: Pre-loaded test data for immediate use
7. **Configurable Storage**: Switch between in-memory and file-based storage

## Logging

The application includes detailed logging for:
- SQL queries (Hibernate SQL logging enabled)
- Database operations
- Transaction processing

## Testing

The H2 database integration can be tested using:
1. H2 Console for direct database queries
2. REST API endpoints using curl or Postman
3. Application logs for transaction verification

## Notes

- The current configuration uses `create-drop` DDL mode, which recreates the schema on each restart
- For production use, switch to `update` mode and use file-based storage
- Transaction processing includes automatic balance validation and updates
- All database operations are transactional for data consistency
