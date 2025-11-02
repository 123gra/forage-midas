package com.jpmc.midascore.Controller;

import com.jpmc.midascore.dto.TransactionDto;
import com.jpmc.midascore.model.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Tells Spring this is a REST Controller
@RequestMapping("/api/transactions") // Makes all methods in this class start with this URL
public class TransactionController {

    @Autowired // Asks Spring to give us the service we just made
    private TransactionService transactionService;

    @PostMapping // This method handles HTTP POST requests
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto transactionDto) {
        // @RequestBody tells Spring to turn the incoming JSON into our TransactionDto

        // 1. Call the service to save the transaction
        Transaction savedTransaction = transactionService.saveTransaction(transactionDto);

        // 2. Return the saved transaction and an "HTTP 201 Created" status
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }
}