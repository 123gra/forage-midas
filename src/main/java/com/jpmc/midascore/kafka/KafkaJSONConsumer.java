package com.jpmc.midascore.kafka;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.payload.TransactionResponse;
import com.jpmc.midascore.service.RestTemplateService;
import com.jpmc.midascore.service.TransactionService;
import com.jpmc.midascore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaJSONConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJSONProducer.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private DatabaseConduit databaseConduit;

    @KafkaListener(topics = "transactions")
    public TransactionResponse consumeTransaction(Transaction transaction){
        LOGGER.info("Received transaction: " + transaction);

        TransactionResponse transactionResponse =
                transactionService.transfer(
                        transaction.getSenderId(),
                        transaction.getRecipientId(),
                        transaction.getAmount());

        UserRecord sender = userService.getUserByID(transaction.getSenderId());
        UserRecord recipient = userService.getUserByID(transaction.getRecipientId());

        if(!transactionResponse.getError() && sender != null && recipient != null) {
            Incentive incentive = restTemplateService.postTransaction(transactionResponse.getTransaction());

            if(incentive.getAmount() > 0) {
                transaction.setIncentive(incentive);
                float newRecipientBalance = recipient.getBalance() + incentive.getAmount();
                recipient.setBalance(newRecipientBalance);
                databaseConduit.save(sender);
                databaseConduit.save(recipient);
            }
        }

        return transactionResponse;
    }
}
