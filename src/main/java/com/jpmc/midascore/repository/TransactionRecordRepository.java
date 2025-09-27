package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    
    /**
     * Find transactions by sender
     */
    List<TransactionRecord> findBySender(UserRecord sender);
    
    /**
     * Find transactions by recipient
     */
    List<TransactionRecord> findByRecipient(UserRecord recipient);
    
    /**
     * Find transactions by sender ID
     */
    @Query("SELECT t FROM TransactionRecord t WHERE t.sender.id = :senderId")
    List<TransactionRecord> findBySenderId(@Param("senderId") Long senderId);
    
    /**
     * Find transactions by recipient ID
     */
    @Query("SELECT t FROM TransactionRecord t WHERE t.recipient.id = :recipientId")
    List<TransactionRecord> findByRecipientId(@Param("recipientId") Long recipientId);
    
    /**
     * Find all transactions for a specific user (either as sender or recipient)
     */
    @Query("SELECT t FROM TransactionRecord t WHERE t.sender.id = :userId OR t.recipient.id = :userId")
    List<TransactionRecord> findAllTransactionsByUserId(@Param("userId") Long userId);
    
    /**
     * Find transactions within a date range
     */
    List<TransactionRecord> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find transactions by amount greater than specified value
     */
    List<TransactionRecord> findByAmountGreaterThan(float amount);
    
    /**
     * Get total sent amount for a user
     */
    @Query("SELECT SUM(t.amount) FROM TransactionRecord t WHERE t.sender.id = :userId")
    Float getTotalSentAmount(@Param("userId") Long userId);
    
    /**
     * Get total received amount for a user
     */
    @Query("SELECT SUM(t.amount) FROM TransactionRecord t WHERE t.recipient.id = :userId")
    Float getTotalReceivedAmount(@Param("userId") Long userId);
    
    /**
     * Count transactions for a user
     */
    @Query("SELECT COUNT(t) FROM TransactionRecord t WHERE t.sender.id = :userId OR t.recipient.id = :userId")
    Long countTransactionsByUserId(@Param("userId") Long userId);
}
