package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@ToString
public class TransactionRecord {

    public TransactionRecord (UserRecord sender, UserRecord recipient, float amount, float incentive){
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

}
