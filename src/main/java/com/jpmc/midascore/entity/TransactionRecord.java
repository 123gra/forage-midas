package com.jpmc.midascore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
public class TransactionRecord {

    @Id
    private long id;
    private long senderId;
    private long recipientId;
    private float amount;

    protected TransactionRecord() {
    }

    public TransactionRecord(long id, long senderId, long recipientId, float amount) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, senderId=%d, recipientId=%d, amount=%f]", id, senderId, recipientId, amount);
    }

    public Long getId() {
        return id;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}
