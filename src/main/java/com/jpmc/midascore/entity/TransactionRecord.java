package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class TransactionRecord{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private UserRecord sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private UserRecord recipient;

    @Column(nullable = false)
    private Float amount;

    @Column(nullable = false)
    private Float incentive; // New field


    protected TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient,float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    public Long getId() {
        return id;
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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getIncentive() {
        return incentive;
    }

    public void setIncentive(Float incentive) {
        this.incentive = incentive;
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getId() : null) +
                ", recipient=" + (recipient != null ? recipient.getId() : null) +
                ", amount=" + amount +
                ", incentive=" + incentive +
                '}';
    }
    
    
}
