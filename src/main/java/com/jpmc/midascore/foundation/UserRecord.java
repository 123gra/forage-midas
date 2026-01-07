package com.jpmc.midascore.foundation;

import jakarta.persistence.*;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Balance balance;

    protected UserRecord() {
    }

    public UserRecord(String name, float initialBalance) {
        this.name = name;
        this.balance = new Balance(initialBalance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(float amount) {
        this.balance.setAmount(amount);
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s', balance=%f]",
                id, name, balance.getAmount()
        );
    }
}
