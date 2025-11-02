package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    // Constructors
    public TransactionRecord() {}
    public TransactionRecord(UserRecord sender, UserRecord recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & setters
    public Long getId() { return id; }
    public double getAmount() { return amount; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
