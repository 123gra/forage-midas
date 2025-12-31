package com.jpmc.midascore.component;

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

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveApiClient incentiveApiClient;

    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository,
            IncentiveApiClient incentiveApiClient) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveApiClient = incentiveApiClient;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender == null || recipient == null) {
            logger.warn("Transaction invalid: Sender or Recipient not found. SenderId: {}, RecipientId: {}",
                    transaction.getSenderId(), transaction.getRecipientId());
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction invalid: Insufficient funds. SenderId: {}, Balance: {}, Amount: {}",
                    sender.getId(), sender.getBalance(), transaction.getAmount());
            return;
        }

        Incentive incentive = incentiveApiClient.getIncentive(transaction);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0;

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        if (sender.getName().equals("waldorf")) {
            logger.info("WALDORF BALANCE UPDATE: {}", sender.getBalance());
        }
        if (recipient.getName().equals("waldorf")) {
            logger.info("WALDORF BALANCE UPDATE: {}", recipient.getBalance());
        }

        if (sender.getName().equals("wilbur")) {
            logger.info("WILBUR BALANCE UPDATE: {}", sender.getBalance());
        }
        if (recipient.getName().equals("wilbur")) {
            logger.info("WILBUR BALANCE UPDATE: {}", recipient.getBalance());
        }

        userRepository.save(sender);
        userRepository.save(recipient);

        // Persist transaction record
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
        transactionRepository.save(record);

        logger.info("Transaction processed successfully: {}", record);
    }
}
