package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a transaction record in the database
 * Maintains many-to-one relationships with sender and recipient users
 */
@Entity
@Table(name = "transaction_records")
public class TransactionRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;
    
    @Column(nullable = false)
    private float amount;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    // Default constructor for JPA
    protected TransactionRecord() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor for creating transaction records
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this();
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserRecord getSender() {
        return sender;
    }
    
    public void setSender(UserRecord sender) {
        this.sender = sender;
    }
    
    public UserRecord getRecipient() {
        return recipient;
    }
    
    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }
    
    public float getAmount() {
        return amount;
    }
    
    public void setAmount(float amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    // Convenience methods to get IDs
    public Long getSenderId() {
        return sender != null ? sender.getId() : null;
    }
    
    public Long getRecipientId() {
        return recipient != null ? recipient.getId() : null;
    }
    
    @Override
    public String toString() {
        return String.format("TransactionRecord{id=%d, senderId=%d, recipientId=%d, amount=%.2f, timestamp=%s}",
                           id, getSenderId(), getRecipientId(), amount, timestamp);
    }
}
