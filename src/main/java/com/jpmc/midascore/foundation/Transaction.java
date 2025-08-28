package com.jpmc.midascore.foundation;

// 📦 Importing Jackson annotation to ignore unknown JSON fields during deserialization
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * 💸 Transaction class models a financial transfer between two users.
 * Includes sender/recipient IDs, transfer amount, and optional incentive.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction
{
    // 🧍 Identifiers for sender and recipient
    private long senderId;
    private long recipientId;

    // 💰 Monetary fields: amount transferred and incentive offered
    private BigDecimal amount;
    private BigDecimal incentive;

    // 🔧 Default constructor initializes amount and incentive to zero
    public Transaction()
    {
        this.amount = BigDecimal.ZERO;
        this.incentive = BigDecimal.ZERO;
    }

    /**
     * 🎯 Constructor for basic transaction without incentive.
     *
     * @param senderId    ID of the sender
     * @param recipientId ID of the recipient
     * @param amount      amount to be transferred
     */
    public Transaction(long senderId, long recipientId, BigDecimal amount)
    {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.incentive = BigDecimal.ZERO;
    }

    // 📥 Getters and Setters for all fields

    public long getSenderId()
    {
        return senderId;
    }

    public void setSenderId(long senderId)
    {
        this.senderId = senderId;
    }

    public long getRecipientId()
    {
        return recipientId;
    }

    public void setRecipientId(long recipientId)
    {
        this.recipientId = recipientId;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getIncentive()
    {
        return incentive;
    }

    public void setIncentive(BigDecimal incentive)
    {
        this.incentive = incentive;
    }

    /**
     * 📌 Custom string representation for logging or debugging.
     *
     * @return formatted string with transaction details
     */
    @Override
    public String toString()
    {
        return "Transaction {senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", amount=" + amount.toPlainString() +
                ", incentive=" + incentive.toPlainString() + "}";
    }
}