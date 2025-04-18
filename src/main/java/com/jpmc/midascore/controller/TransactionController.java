package com.jpmc.midascore.controller;

import com.jpmc.midascore.dto.TransactionView;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionController(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    // ✅ GET /transactions — used by TaskFiveTests
    @GetMapping("/transactions")
    public List<TransactionView> getTransactions() {
        return transactionRepository.findAll().stream()
                .map(t -> new TransactionView(
                        t.getSender().getName(),
                        t.getRecipient().getName(),
                        t.getAmount(),
                        t.getIncentive()))
                .collect(Collectors.toList());
    }

    // ✅ GET /balance — supports ?userId= (for test) and none (for manual)
    @GetMapping("/balance")
    public Object getBalance(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return userRepository.findById(userId)
                    .map(user -> new Balance(user.getId(), user.getBalance()))
                    .orElse(new Balance(userId, 0f));
        } else {
            Map<String, Float> balances = new HashMap<>();
            userRepository.findAll().forEach(user -> {
                balances.put(user.getName(), user.getBalance());
            });
            return balances;
        }
    }
}
