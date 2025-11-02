package com.jpmc.midascore.service;

import com.jpmc.midascore.dto.TransactionDto;
import com.jpmc.midascore.model.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Tells Spring this is a Service class
public class TransactionService {

    @Autowired // Asks Spring to give us the repository we made in Task 2
    private TransactionRepository transactionRepository;

    public Transaction saveTransaction(TransactionDto dto) {
        // 1. Convert the DTO (web data) into an Entity (database data)
        Transaction transaction = new Transaction(
                dto.accountId(),
                dto.amount(),
                dto.timestamp()
        );

        // 2. Save the entity using the repository
        return transactionRepository.save(transaction);
    }
}