package com.jpmc.midascore.foundation;

// 📦 Importing Jackson annotation to ignore unknown JSON fields during deserialization
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * 💰 Balance class represents a monetary value associated with a user.
 * Designed for flexibility in construction and safe deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Balance
{
    // 🧮 Core field representing the monetary amount
    private BigDecimal amount;

    // 🔧 Default constructor initializes balance to zero
    public Balance()
    {
        this.amount = BigDecimal.ZERO;
    }

    /**
     * 🎯 Constructor accepting a BigDecimal amount directly.
     *
     * @param amount the monetary value
     */
    public Balance(BigDecimal amount)
    {
        this.amount = amount;
    }

    /**
     * 🧍 Constructor accepting userId and balance.
     * Ensures null-safe initialization of amount.
     *
     * @param userId  the user's identifier (unused here but may be relevant in context)
     * @param balance the monetary value, defaults to zero if null
     */
    public Balance(Long userId, BigDecimal balance)
    {
        this.amount = balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * 🧍 Constructor accepting userId and primitive long balance.
     * Converts long to BigDecimal for precision.
     *
     * @param userId  the user's identifier
     * @param balance the monetary value as long
     */
    public Balance(Long userId, long balance)
    {
        this.amount = BigDecimal.valueOf(balance);
    }

    // 📥 Getter for amount
    public BigDecimal getAmount()
    {
        return amount;
    }

    // 🔧 Setter for amount
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    /**
     * 📌 Custom string representation for logging or debugging.
     *
     * @return formatted string with balance details
     */
    @Override
    public String toString() {
        return "Balance {amount=" + amount.toPlainString() + "}";
    }
}