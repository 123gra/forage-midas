package com.jpmc.midascore.model;


import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.*;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

    public TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    public float getIncentive() {return incentive;}
    public void setIncentive(float incentive){this.incentive = incentive;}
    public long getId() {return id;}
    public UserRecord getSender() {return sender;}
    public UserRecord getRecipient() {return recipient;}
    public float getAmount() {return amount;}
}
