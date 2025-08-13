package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import org.apache.catalina.LifecycleState;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue()
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float balance;

    @OneToMany(mappedBy = "Sender")
    private List<TransactionRecord> senderTransactions;

    @OneToMany(mappedBy = "Recipient")
    private List<TransactionRecord> recipientTransactions ;


    protected UserRecord() {
    }

    public UserRecord(String name, float balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', balance='%f'", id, name, balance);
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
}
