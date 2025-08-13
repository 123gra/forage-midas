package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.Sender = sender;
        this.Recipient = recipient;
        this.amount = amount;

    }

    @Id
    @GeneratedValue()
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord Sender;

    @ManyToOne
    @JoinColumn(name = "recipent_id")
    private UserRecord Recipient;

    private float amount;




}
