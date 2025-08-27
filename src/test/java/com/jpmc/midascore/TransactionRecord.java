package com.jpmc.midascore;

// Task 3

import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord recipient;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal incentive;

    public TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, BigDecimal amount, BigDecimal incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = (amount != null ? amount : BigDecimal.ZERO);
        this.incentive = (incentive != null ? incentive : BigDecimal.ZERO);
    }

    public Long getId() {
        return id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getIncentive() {
        return incentive;
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getName() : "null") +
                ", recipient=" + (recipient != null ? recipient.getName() : "null") +
                ", amount=" + (amount != null ? amount.toPlainString() : "null") +
                ", incentive=" + (incentive != null ? incentive.toPlainString() : "null") +
                '}';
    }
}
