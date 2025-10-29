# Task: Expose REST API for Querying User Balances

## Steps
- [x] Edit application.yml to add server.port: 33400
- [x] Create BalanceController.java with GET /balance endpoint, inject UserRepository, return Balance JSON (0 if user not found)
- [x] Start Incentive API JAR in background: java -jar services/transaction-incentive-api.jar
- [ ] Run mvn test -Dtest=TaskFiveTests
- [ ] Capture and submit the output between ---begin output --- and ---end output ---
