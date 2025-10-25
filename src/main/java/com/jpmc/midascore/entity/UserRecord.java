package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionRecord> sentTransactions = new HashSet<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionRecord> receivedTransactions = new HashSet<>();

    protected UserRecord() {
    }

    public UserRecord(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', balance='%s']", id, name, balance.toPlainString());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Set<TransactionRecord> getSentTransactions() {
        return sentTransactions;
    }

    public void setSentTransactions(Set<TransactionRecord> sentTransactions) {
        this.sentTransactions = sentTransactions;
    }

    public Set<TransactionRecord> getReceivedTransactions() {
        return receivedTransactions;
    }

    public void setReceivedTransactions(Set<TransactionRecord> receivedTransactions) {
        this.receivedTransactions = receivedTransactions;
    }
}
