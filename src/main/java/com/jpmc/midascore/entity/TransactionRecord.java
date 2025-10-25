package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_record")
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
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal incentive;

    public TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, BigDecimal amount, LocalDateTime timestamp, BigDecimal incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
        this.incentive = incentive;
    }

    // Getters and Setters
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getIncentive() {
        return incentive;
    }

    public void setIncentive(BigDecimal incentive) {
        this.incentive = incentive;
    }
}
