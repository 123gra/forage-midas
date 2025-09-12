package com.jpmc.midascore.foundation;

/**
 * Enum representing the status of a transaction
 */
public enum TransactionStatus {
    PENDING("Transaction is pending processing"),
    COMPLETED("Transaction has been completed successfully"),
    FAILED("Transaction has failed"),
    CANCELLED("Transaction has been cancelled");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
