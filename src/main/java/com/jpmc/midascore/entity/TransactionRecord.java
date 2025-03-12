package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import com.jpmc.midascore.entity.UserRecord;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

    protected TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    public float getIncentive() { return incentive; }

    public void setIncentive(float incentive) { this.incentive = incentive; }

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, senderId=%d, recipientId=%d, amount=%f]", id, sender, recipient, amount);
    }

    public Long getId() {
        return id;
    }

    public UserRecord getSender() { return sender; }

    public UserRecord getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}
