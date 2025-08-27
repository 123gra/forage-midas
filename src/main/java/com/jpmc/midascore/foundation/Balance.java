package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Balance {

    private BigDecimal amount;

    public Balance() {
        this.amount = BigDecimal.ZERO;
    }

    public Balance(BigDecimal amount) {
        this.amount = amount;
    }

    public Balance(Long userId, BigDecimal balance) {
        this.amount = balance != null ? balance : BigDecimal.ZERO;
    }

    public Balance(Long userId, long balance) {
        this.amount = BigDecimal.valueOf(balance);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Balance {amount=" + amount.toPlainString() + "}";
    }
}