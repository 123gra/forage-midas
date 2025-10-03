package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    public float getUserBalanceById(Long userId){
        UserRecord user = userRepository.findById(userId).orElse(null);
        if(user == null){
            log.info("User with userId: {} not found!", userId);
            return 0.0f;
        }

        return user.getBalance();

    }

    @Transactional
    public void process(Transaction tx) {
        // 1) Validate users
        UserRecord sender = userRepository.findById(tx.getSenderId());
        UserRecord recipient = userRepository.findById(tx.getRecipientId());
        if (sender == null || recipient == null) {
            log.debug("Discarding tx: invalid sender/recipient (senderId={}, recipientId={})",
                    tx.getSenderId(), tx.getRecipientId());
            return;
        }

        Incentive incentive = incentiveService.getIncentive(tx);
        float incentiveAmount = incentive.getAmount();

        // 2) Validate funds
        if (sender.getBalance() < tx.getAmount()) {
            log.debug("Discarding tx: insufficient funds (senderId={}, bal={}, amt={})",
                    sender.getId(), sender.getBalance(), tx.getAmount());
            return;
        }

        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);
        userRepository.save(sender);
        userRepository.save(recipient);

        log.info("User: {}, balance: {}", recipient.getName(), recipient.getBalance());

        TransactionRecord record = new TransactionRecord(sender, recipient, tx.getAmount(), incentiveAmount);
        transactionRepository.save(record);

        log.info("Processed transaction: sender={}, recipient={}, amt={}, incentive={}",
                sender.getId(), recipient.getId(), tx.getAmount(), incentiveAmount);
    }
}