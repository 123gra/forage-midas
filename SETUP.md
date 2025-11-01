# Setup Instructions for TaskFourTests

## Prerequisites

Before running TaskFourTests in debug mode, ensure all required services are running:

## Step 1: Start the Incentive API

The Incentive API must be running on port 8080. Start it using:

```bash
./start-incentive-api.sh
```

Or manually:
```bash
java -jar services/transaction-incentive-api.jar
```

Verify it's running by checking:
```bash
curl http://localhost:8080/incentive
```

## Step 2: Kafka Setup

The test uses `@EmbeddedKafka` which automatically starts an embedded Kafka instance. However, if you encounter connection issues:

### Option A: Use EmbeddedKafka (Recommended)
The test will automatically start an embedded Kafka. Just ensure no other Kafka is running on port 9092.

### Option B: Use External Kafka with Docker
If you prefer external Kafka:

```bash
docker-compose up -d
```

This will start Zookeeper (port 2181) and Kafka (port 9092).

**Note:** If using external Kafka, you may need to remove `@EmbeddedKafka` annotation from the test.

## Step 3: Run TaskFourTests in Debug Mode

1. Set a breakpoint on the line with the comment: `// BREAKPOINT HERE: All transactions should be processed now`
2. Run `TaskFourTests.task_four_verifier()` in debug mode
3. The test will:
   - Populate users
   - Send all transactions
   - Wait 5 seconds for processing
   - Hit your breakpoint
   - Pause execution for debugging

## Step 4: Find Wilbur's Balance

Once the debugger pauses at the breakpoint, evaluate in the debugger:

```java
userRepository.findByName("wilbur").getBalance()
```

Or:

```java
userRepository.findById(9L).getBalance()
```

Round down to the nearest integer as required.

## Troubleshooting

- **"Connection to node -1 could not be established"**: Kafka isn't running. Check if EmbeddedKafka started or start external Kafka.
- **"Evaluation failed because thread is not suspended"**: The breakpoint wasn't hit. Ensure you set the breakpoint after the `Thread.sleep(5000)` line.
- **Incentive API errors**: Ensure the Incentive API is running on port 8080. Check logs for connection errors.

