package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionHandler {

    private final DatabaseConduit databaseConduit;
    private final IncentiveFetchService incentiveFetchService;

    public void processRequestedTransaction(Transaction transaction) {

        if (databaseConduit.isValidTransaction(transaction)) {
            Incentive incentive = incentiveFetchService.fetchIncentive(transaction);
            transaction.setIncentive(incentive.getAmount());
            databaseConduit.save(transaction);
        }
    }

}
