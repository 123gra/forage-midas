package com.jpmc.midascore;

// 📦 Importing JPA annotations and BigDecimal for monetary precision
import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 🧾 TransactionRecord is a JPA entity that logs completed transactions.
 * It captures sender, recipient, amount transferred, and any incentive applied.
 */
@Entity
public class TransactionRecord
{
    // 🔑 Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🧍 Sender of the transaction (many transactions can share the same sender)
    @ManyToOne
    private UserRecord sender;

    // 🧍 Recipient of the transaction
    @ManyToOne
    private UserRecord recipient;

    // 💰 Amount transferred (required)
    @Column(nullable = false)
    private BigDecimal amount;

    // 🎁 Incentive applied to the transaction (required)
    @Column(nullable = false)
    private BigDecimal incentive;

    // 🔒 Default constructor for JPA
    public TransactionRecord() {}

    /**
     * 🎯 Constructor for creating a transaction record.
     * Ensures null-safe initialization of monetary fields.
     *
     * @param sender    the user sending the funds
     * @param recipient the user receiving the funds
     * @param amount    the amount transferred
     * @param incentive the incentive applied
     */
    public TransactionRecord(UserRecord sender, UserRecord recipient, BigDecimal amount, BigDecimal incentive)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = (amount != null ? amount : BigDecimal.ZERO);
        this.incentive = (incentive != null ? incentive : BigDecimal.ZERO);
    }

    // 📥 Getters for accessing private fields
    public Long getId() { return id; }

    public UserRecord getSender() { return sender; }

    public UserRecord getRecipient() { return recipient; }

    public BigDecimal getAmount() { return amount; }

    public BigDecimal getIncentive() { return incentive; }

    /**
     * 📌 Custom string representation for logging or debugging.
     *
     * @return formatted string with transaction details
     */
    @Override
    public String toString()
    {
        return "TransactionRecord{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getName() : "null") +
                ", recipient=" + (recipient != null ? recipient.getName() : "null") +
                ", amount=" + (amount != null ? amount.toPlainString() : "null") +
                ", incentive=" + (incentive != null ? incentive.toPlainString() : "null") +
                '}';
    }
}