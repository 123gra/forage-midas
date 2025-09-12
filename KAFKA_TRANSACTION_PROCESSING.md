# Midas Core - Kafka Transaction Processing

This document describes the complete Kafka-based transaction processing implementation for Midas Core.

## Overview

Midas Core now fully supports receiving transactions via Kafka, validating them according to business rules, and recording them to the H2 database. The system ensures data integrity through comprehensive validation and proper transaction management.

## Architecture

### Components

1. **KafkaTransactionConsumer** - Listens to Kafka transactions
2. **TransactionValidator** - Validates transactions according to business rules
3. **TransactionProcessor** - Orchestrates validation and database operations
4. **DatabaseConduit** - Handles database operations
5. **TransactionMonitoringService** - Monitors and logs transaction processing
6. **MonitoringController** - REST API for monitoring statistics

### Validation Rules

A transaction is considered **valid** if all of the following are true:
- ✅ **SenderId is valid** - Sender user exists in the database
- ✅ **RecipientId is valid** - Recipient user exists in the database  
- ✅ **Sender has sufficient balance** - Balance >= transaction amount
- ✅ **Amount is positive** - Transaction amount > 0
- ✅ **Sender ≠ Recipient** - Cannot transfer to yourself

## Configuration

### Kafka Configuration (`application.yml`)
```yaml
general:
  kafka-topic: transaction-topic

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: midas-core-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.jpmc.midascore.foundation"
        spring.json.type.mapping: "transaction:com.jpmc.midascore.foundation.Transaction"
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    listener:
      ack-mode: record
```

## Processing Flow

```
1. Transaction received via Kafka
    ↓
2. TransactionValidator validates business rules
    ↓
3. If VALID: TransactionProcessor executes money transfer
   If INVALID: Transaction marked as FAILED and saved
    ↓
4. Database updated with transaction record
    ↓
5. User balances updated (if successful)
    ↓
6. Result logged via TransactionMonitoringService
```

## Transaction Processing Scenarios

### 1. Successful Transaction
```json
{
  "senderId": 1,
  "recipientId": 2,
  "amount": 100.0
}
```
**Result**: 
- Status: `COMPLETED`
- Sender balance: decreased by 100.0
- Recipient balance: increased by 100.0
- Transaction saved to database

### 2. Invalid Sender
```json
{
  "senderId": 999,
  "recipientId": 2,
  "amount": 100.0
}
```
**Result**:
- Status: `FAILED`
- Reason: "Sender with ID 999 not found"
- No balance changes
- Failed transaction saved to database

### 3. Insufficient Balance
```json
{
  "senderId": 1,
  "recipientId": 2,
  "amount": 10000.0
}
```
**Result**:
- Status: `FAILED` 
- Reason: "Insufficient balance for sender 1. Balance: 1000.00, Required: 10000.00"
- No balance changes
- Failed transaction saved to database

## Database Schema Impact

### Transaction Table Updates
The `Transaction` entity now includes:
- `id` (auto-generated)
- `senderId` 
- `recipientId`
- `amount`
- `timestamp` (automatically set)
- `status` (PENDING/COMPLETED/FAILED/CANCELLED)

## API Endpoints

### Monitoring Endpoints
- `GET /api/monitoring/stats` - Get transaction processing statistics
- `GET /api/monitoring/check-failures` - Trigger manual failure threshold check

### Example Response: Transaction Stats
```json
{
  "totalTransactions": 150,
  "successfulTransactions": 142,
  "failedTransactions": 8,
  "pendingTransactions": 0,
  "successRate": 94.67,
  "failureRate": 5.33
}
```

## Error Handling

### Validation Errors
- Invalid sender/recipient IDs
- Insufficient balance
- Negative amounts
- Self-transfers

### System Errors  
- Database connection issues
- Kafka processing errors
- Unexpected exceptions

### Error Response Strategy
1. **Always acknowledge Kafka messages** (prevents infinite retries)
2. **Save failed transactions** to database for audit trail
3. **Log detailed error information** for monitoring
4. **Continue processing** other messages

## Monitoring & Logging

### Log Levels
- `INFO` - Successful transaction processing
- `WARN` - Validation failures
- `ERROR` - System errors and processing failures
- `DEBUG` - Detailed processing information

### Monitoring Features
- **Real-time statistics** - Success/failure rates over 24 hours
- **Failure threshold alerts** - Warns when failures exceed 10/hour
- **Asynchronous logging** - Non-blocking monitoring operations
- **Comprehensive audit trail** - All transactions logged with details

## Testing

### Integration Tests
The system includes comprehensive integration tests covering:
- ✅ Successful transaction processing
- ✅ Invalid sender scenarios
- ✅ Invalid recipient scenarios  
- ✅ Insufficient balance scenarios
- ✅ Negative amount validation
- ✅ Self-transfer prevention

### Running Tests
```bash
# Run integration tests
./mvnw test -Dtest=KafkaTransactionProcessingIntegrationTest

# Run all tests
./mvnw test
```

## Usage Examples

### 1. Send Valid Transaction via Kafka Producer (Test)
```java
Transaction transaction = new Transaction(1L, 2L, 100.0f);
kafkaTemplate.send("transaction-topic", transaction);
```

### 2. Monitor Transaction Statistics
```bash
curl http://localhost:8080/api/monitoring/stats
```

### 3. Check Recent Failures
```bash
curl http://localhost:8080/api/monitoring/check-failures
```

### 4. View Processed Transactions
```bash
curl http://localhost:8080/api/transactions/status/COMPLETED
curl http://localhost:8080/api/transactions/status/FAILED
```

## Performance Considerations

### Kafka Consumer
- **Manual acknowledgment** - Ensures message processing before commit
- **Single consumer group** - Maintains transaction order
- **Error handling** - Prevents poison message loops

### Database Operations
- **Transactional processing** - ACID compliance for money transfers
- **Connection pooling** - HikariCP for efficient database connections
- **Batch operations** - Where applicable for better performance

### Monitoring
- **Asynchronous logging** - Non-blocking monitoring operations
- **Configurable thresholds** - Adjustable failure rate alerts

## Security Considerations

### Message Validation
- **Trusted packages** configured for JSON deserialization
- **Type mapping** prevents arbitrary object deserialization
- **Input validation** on all transaction fields

### Database Security
- **Transactional integrity** - Prevents partial updates
- **Balance validation** - Prevents overdrafts
- **Audit trail** - All transactions logged with timestamps

## Deployment Notes

### Prerequisites
1. **Kafka broker** running on localhost:9092 (or configure different host)
2. **H2 database** configured and running
3. **Topic exists**: `transaction-topic`

### Environment Variables
```bash
# Optional Kafka broker override
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Optional topic name override  
KAFKA_TOPIC=transaction-topic
```

### Health Checks
- Monitor transaction processing statistics
- Check application logs for errors
- Verify Kafka consumer is connected and processing

## Troubleshooting

### Common Issues

**1. Kafka Connection Errors**
- Verify Kafka broker is running
- Check network connectivity
- Validate bootstrap server configuration

**2. High Failure Rates** 
- Check user data integrity
- Verify balance consistency
- Review validation error logs

**3. Performance Issues**
- Monitor database connection pool
- Check Kafka consumer lag
- Review async processing logs

### Monitoring Commands
```bash
# Check transaction statistics
curl http://localhost:8080/api/monitoring/stats

# View recent failed transactions  
curl http://localhost:8080/api/transactions/status/FAILED

# Check application health
curl http://localhost:8080/actuator/health
```

The Kafka transaction processing system is now fully implemented and ready for production use! 🚀
