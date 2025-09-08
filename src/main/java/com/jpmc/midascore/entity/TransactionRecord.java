package com.jpmc.midascore.entity;

import org.hibernate.annotations.ManyToAny;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue()
    private long id;

    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord receiver;

    private float ammount;

    public TransactionRecord(UserRecord sender, UserRecord receiver, float ammount){
        this.sender = sender;
        this.receiver = receiver;
        this.ammount = ammount;
    }

    public long getId(){
        return id;
    }
    public UserRecord getSender(){
        return sender;
    }

    public UserRecord getReceiver(){
        return receiver;
    }

    public float getAmmount(){
        return ammount;
    }

}
