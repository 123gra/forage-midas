package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    private long senderId;
    private long recipientId;
    private BigDecimal amount;
    private BigDecimal incentive;

    public Transaction() {
        this.amount = BigDecimal.ZERO;
        this.incentive = BigDecimal.ZERO;
    }

    public Transaction(long senderId, long recipientId, BigDecimal amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.incentive = BigDecimal.ZERO;
    }

    public Transaction(long senderId, long recipientId, float v) {
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getIncentive() {
        return incentive;
    }

    public void setIncentive(BigDecimal incentive) {
        this.incentive = incentive;
    }

    @Override
    public String toString() {
        return "Transaction {senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", amount=" + amount.toPlainString() +
                ", incentive=" + incentive.toPlainString() + "}";
    }
}
