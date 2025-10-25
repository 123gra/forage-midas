package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Double balance;

    public User() {}

    public User(String name, String email, Double balance) {
        this.name = name;
        this.email = email;
        this.balance = balance;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
}