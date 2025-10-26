package com.jpmc.midascore.entity;


import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue()
    private Long id;

    private float  amount;

    @ManyToOne
    @JoinColumn(name="sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name="recipient_id")
    private UserRecord recipient;

    //getter and setters
    public Long getId() {return id;}
    public float getAmount() {return amount;}
    public void setAmount(float amount) {this.amount = amount;}

    public UserRecord getSender() {return sender;}
    public void setSender(UserRecord sender) {this.sender = sender;}

    public UserRecord getRecipient() {return recipient;}
    public void setRecipient(UserRecord recipient) {this.recipient = recipient;}
}
