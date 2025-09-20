package com.jpmc.midascore.controller;

import com.jpmc.midascore.dto.TransactionDTO;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public void recordTransaction(@RequestBody TransactionDTO transactionDTO) {
        transactionService.recordTransaction(
                transactionDTO.getAccountId(),
                transactionDTO.getAmount()
        );
    }
}