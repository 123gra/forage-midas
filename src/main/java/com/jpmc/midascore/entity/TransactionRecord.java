package com.jpmc.midascore.entity;


import jakarta.persistence.*;
import org.apache.catalina.User;
import com.jpmc.midascore.entity.UserRecord;

import java.math.BigDecimal;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private UserRecord sender;


    @ManyToOne
    @JoinColumn(name = "recipient_id",nullable = false)
    private UserRecord recipient;

    public BigDecimal getIncentive() {
        return incentive;
    }

    public void setIncentive(BigDecimal incentive) {
        this.incentive = incentive;
    }

    private BigDecimal incentive;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    private BigDecimal amount;

    public TransactionRecord(){

    }

    public TransactionRecord(UserRecord sender,UserRecord recipient,BigDecimal amount){

        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;



    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }

    public UserRecord getSender() {
        return sender;
    }

    public void setSender(UserRecord sender) {
        this.sender = sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
