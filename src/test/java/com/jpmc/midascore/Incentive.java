package com.jpmc.midascore;

// 📦 Importing BigDecimal for precise monetary representation
import java.math.BigDecimal;

/**
 * 🎁 Incentive class represents a monetary reward or bonus.
 * Designed for flexible instantiation and JSON compatibility.
 */
public class Incentive
{
    // 💰 Amount of the incentive
    private BigDecimal amount;

    /**
     * 🛠️ Default constructor required for JSON deserialization.
     * Initializes an empty Incentive object.
     */
    public Incentive()
    {
        // No initialization needed; amount can be set later
    }

    /**
     * 🎯 Constructor for manual creation of Incentive.
     *
     * @param amount the monetary value of the incentive
     */
    public Incentive(BigDecimal amount)
    {
        this.amount = amount;
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
}