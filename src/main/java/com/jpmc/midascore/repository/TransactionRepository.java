package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends CrudRepository<TransactionRecord, Long> {
    List<TransactionRecord> findBySenderId(long senderId);
    List<TransactionRecord> findByRecipientId(long recipientId);
}
