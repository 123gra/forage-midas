package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void recordTransaction(String accountId, BigDecimal amount) {
        Transaction newTransaction = new Transaction(
                accountId,
                amount,
                Instant.now()
        );

        transactionRepository.save(newTransaction);
    }
}