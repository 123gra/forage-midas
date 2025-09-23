package com.jpmc.midascore.repository;

import com.jpmc.midascore.foundation.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {

    // You can add custom queries later if needed
}
