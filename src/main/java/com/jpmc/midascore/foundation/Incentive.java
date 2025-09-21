package com.jpmc.midascore.foundation;

import java.util.Objects;

public class Incentive {
    private float amount;

    public Incentive(float amount) {
        this.amount = amount;
    }

    public Incentive() {}

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Incentive incentive = (Incentive) o;
        return Float.compare(amount, incentive.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}
