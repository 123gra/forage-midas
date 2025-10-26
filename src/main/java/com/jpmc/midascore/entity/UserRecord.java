package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class UserRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue()
    private long id;
    private long senderId;
    private long recipientId;
  

    @Column(nullable = false)
    private String name;
    private float amount;
    private double incentive;

    @Column(nullable = false)
    private float balance;

    public UserRecord() {
    }

    public UserRecord(String name, float balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', balance='%f']", id, name, balance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }
   
    public void setSenderId(long senderId) {
    this.senderId = senderId;
}

public void setRecipientId(long recipientId) {
    this.recipientId = recipientId;
}

public void setAmount(float amount) {
    this.amount = amount;
}

public void setIncentive(double incentive) {
    this.incentive = incentive;
}

}
