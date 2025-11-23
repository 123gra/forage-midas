package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final DatabaseConduit databaseConduit;

    public void processRequestedTransaction(Transaction transaction) {

        if (databaseConduit.isValidTransaction(transaction)) {
            databaseConduit.save(transaction);
        }
    }

}
