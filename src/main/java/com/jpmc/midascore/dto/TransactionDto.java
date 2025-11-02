package com.jpmc.midascore.dto;

import java.math.BigDecimal;
import java.time.Instant;

// This is a Java 17 "record", which is a modern, simple way 
// to create a class that just holds data.
// It automatically creates the fields, constructor, getters,
// equals(), and hashCode() methods for you.
public record TransactionDto(
    String accountId,
    BigDecimal amount,
    Instant timestamp
) {
}