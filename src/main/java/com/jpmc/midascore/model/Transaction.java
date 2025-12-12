
package com.jpmc.midascore.model;

import java.math.BigDecimal;

public class Transaction {
    private String id;
    private String type;
    private BigDecimal amount;
    private String timestamp;
    private String accountId;

    public Transaction() {
    }

    public Transaction(String id, String type, BigDecimal amount, String timestamp, String accountId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.accountId = accountId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", timestamp='" + timestamp + '\'' +
                ", accountId='" + accountId + '\'' +
                '}';
    }
}
