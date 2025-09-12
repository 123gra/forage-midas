package com.jpmc.midascore.repository;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find transactions by sender ID
     */
    List<Transaction> findBySenderId(long senderId);
    
    /**
     * Find transactions by recipient ID
     */
    List<Transaction> findByRecipientId(long recipientId);
    
    /**
     * Find transactions by status
     */
    List<Transaction> findByStatus(TransactionStatus status);
    
    /**
     * Find all transactions for a specific user (either as sender or recipient)
     */
    @Query("SELECT t FROM Transaction t WHERE t.senderId = :userId OR t.recipientId = :userId")
    List<Transaction> findAllTransactionsByUserId(@Param("userId") long userId);
    
    /**
     * Find transactions within a date range
     */
    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find transactions by sender and status
     */
    List<Transaction> findBySenderIdAndStatus(long senderId, TransactionStatus status);
    
    /**
     * Find transactions by recipient and status
     */
    List<Transaction> findByRecipientIdAndStatus(long recipientId, TransactionStatus status);
    
    /**
     * Count transactions by status
     */
    long countByStatus(TransactionStatus status);
    
    /**
     * Find transactions by amount greater than specified value
     */
    List<Transaction> findByAmountGreaterThan(float amount);
    
    /**
     * Get total transaction amount for a user
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.senderId = :userId AND t.status = :status")
    Float getTotalSentAmount(@Param("userId") long userId, @Param("status") TransactionStatus status);
    
    /**
     * Get total received amount for a user
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.recipientId = :userId AND t.status = :status")
    Float getTotalReceivedAmount(@Param("userId") long userId, @Param("status") TransactionStatus status);
}
