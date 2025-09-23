package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private long senderId;
    private long recipientId;
    private float amount;

    // No-args constructor
    public Transaction() {
    }

    // All-args constructor with @JsonCreator for Jackson deserialization
    @JsonCreator
    public Transaction(@JsonProperty("senderId") long senderId,
                       @JsonProperty("recipientId") long recipientId,
                       @JsonProperty("amount") float amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    // Getters and setters
    public long getSenderId() { return senderId; }
    public void setSenderId(long senderId) { this.senderId = senderId; }

    public long getRecipientId() { return recipientId; }
    public void setRecipientId(long recipientId) { this.recipientId = recipientId; }

    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "Transaction {senderId=" + senderId + ", recipientId=" + recipientId + ", amount=" + amount + "}";
    }
}
