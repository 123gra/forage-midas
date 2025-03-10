package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import com.jpmc.midascore.entity.UserRecord;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord senderId;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipientId;

    private float amount;

    protected TransactionRecord() {
    }

    public TransactionRecord(UserRecord senderId, UserRecord recipientId, float amount) {
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

    public UserRecord getSenderId() { return senderId; }

    public UserRecord getRecipientId() {
        return recipientId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}
