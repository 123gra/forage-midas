package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private long senderId;
    private long recipientId;
    private float amount;
    public Float incentive;

    public Transaction() {
    }

//    public Transaction(long senderId, long recipientId, float amount) {
//        this.senderId = senderId;
//        this.recipientId = recipientId;
//        this.amount = amount;
//        this.incentive = 0.0f;
//    }

    public Transaction(long senderId, long recipientId, float amount, Float incentive) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.incentive = incentive;
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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Float getIncentive() {return incentive;}

    public  void setIncentive(Float incentive) {this.incentive = incentive;}

    @Override
    public String toString() {
        return "Transaction {senderId=" + senderId + ", recipientId=" + recipientId + ", amount=" + amount + "}";
    }
}
