package com.jpmc.midascore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float amount;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public TransactionRecord() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRecord that = (TransactionRecord) o;
        return id == that.id && Float.compare(amount, that.amount) == 0 && Objects.equals(sender, that.sender) && Objects.equals(recipient, that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, sender, recipient);
    }

//    @Override
//    public String toString() {
//        return "TransactionRecord{" +
//                "id=" + id +
//                ", amount=" + amount +
//                ", sender=" + (sender != null ? sender.getName() + " (balance=" + sender.getBalance() + ")" : null) +
//                ", recipient=" + (recipient != null ? recipient.getName() + " (balance=" + recipient.getBalance() + ")" : null) +
//                '}';
//    }
}
