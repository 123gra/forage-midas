package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionUtils {
    public static List<Float> getFirstFourAmounts(List<Transaction> transactions) {
        return transactions.stream()
                .limit(4)                        // take first 4
                .map(Transaction::getAmount)     // extract amounts
                .collect(Collectors.toList());   // collect into list
    }
}