package com.jpmc.midascore.entity;

// 📦 Importing JPA annotations and BigDecimal for monetary precision
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 🧾 Entity representing a user record in the database.
 * Includes fields for ID, name, and account balance.
 */
@Entity
public class UserRecord
{
    // 🔑 Primary key with auto-generation strategy
    @Id
    @GeneratedValue
    private Long id;

    // 🧍 User's name (required field)
    @Column(nullable = false)
    private String name;

    // 💰 User's account balance with high precision for financial accuracy
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    // 🔒 Protected no-arg constructor for JPA
    protected UserRecord() {}

    /**
     * 🎯 Public constructor to create a new UserRecord instance.
     *
     * @param name    the user's name
     * @param balance the user's account balance
     */
    public UserRecord(String name, BigDecimal balance)
    {
        this.name = name;
        this.balance = balance;
    }

    /**
     * 📌 Custom string representation for logging or debugging.
     *
     * @return formatted string with user details
     */
    @Override
    public String toString()
    {
        return String.format("User[id=%d, name='%s', balance='%s']",
                id, name, balance.toPlainString());
    }

    // 📥 Getters for accessing private fields
    public Long getId() { return id; }

    public String getName() { return name; }

    public BigDecimal getBalance() { return balance; }

    // 🔧 Setter for updating balance
    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }
}