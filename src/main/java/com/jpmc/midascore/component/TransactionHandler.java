//package com.jpmc.midascore.component;
//
//import com.jpmc.midascore.foundation.Incentive;
//import com.jpmc.midascore.foundation.Transaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TransactionHandler {
//    static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);
//    private final DatabaseConduit databaseConduit;
//    private final IncentiveQuerier incentiveQuerier;
//
//    public TransactionHandler(DatabaseConduit databaseConduit, IncentiveQuerier incentiveQuerier) {
//        this.databaseConduit = databaseConduit;
//        this.incentiveQuerier = incentiveQuerier;
//    }
//
//    public void handleTransaction(Transaction transaction) {
//        if (databaseConduit.isValid(transaction)) {
//            Incentive incentive = incentiveQuerier.query(transaction);
//            transaction.setIncentive(incentive.getAmount());
//            databaseConduit.save(transaction);
//        }
//    }
//}
package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionHandler {
    static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);
    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;

    public TransactionHandler(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
        this.restTemplate = new RestTemplate(); // Create a new RestTemplate instance
    }

    public void handleTransaction(Transaction transaction) {
        if (databaseConduit.isValid(transaction)) {
            //  POST to Incentive API
            Incentive incentive = restTemplate.postForObject(
                    "http://localhost:8080/incentive",
                    transaction,
                    Incentive.class
            );

            // To Add incentive to the transaction
            if (incentive != null) {
                transaction.setIncentive(incentive.getAmount());
            } else {
                transaction.setIncentive(0);
            }

            // To Save transaction with incentive
            databaseConduit.save(transaction);
        }
    }
}
