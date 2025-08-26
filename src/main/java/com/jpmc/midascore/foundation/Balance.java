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

    public Balance(Long userId, long l) {
        // you can initialize amount here if needed
        this.amount = BigDecimal.ZERO;
    }

    public Balance(Long id, BigDecimal balance) {
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