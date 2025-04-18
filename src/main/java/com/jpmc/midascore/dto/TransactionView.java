package com.jpmc.midascore.dto;

public class TransactionView {
    private String sender;
    private String recipient;
    private float amount;
    private float incentive;

    public TransactionView(String sender, String recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public float getIncentive() {
        return incentive;
    }
}
