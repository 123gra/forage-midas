package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Balance {
    private double amount;

    public Balance() {
    }

    public Balance(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        String formatted = String.format("%.2f", amount);  // Always format to 2 decimal places initially
        // If ends in ".00", truncate to whole number
        if (formatted.endsWith(".00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }
        // If ends in "0" after decimal, remove it (e.g. "1.20" -> "1.2")
        else if (formatted.charAt(formatted.length() - 1) == '0') {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return "Balance {amount=" + formatted + "}";
    }
}