package com.jpmc.midascore.controller;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for transaction-related operations
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private final DatabaseConduit databaseConduit;
    
    public TransactionController(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }
    
    /**
     * Get transaction record by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionRecord> getTransactionById(@PathVariable long id) {
        Optional<TransactionRecord> transaction = databaseConduit.findTransactionRecordById(id);
        return transaction.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all transaction records for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionRecord>> getUserTransactions(@PathVariable long userId) {
        List<TransactionRecord> transactions = databaseConduit.findUserTransactionRecords(userId);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get recent transaction records (last 24 hours)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionRecord>> getRecentTransactions() {
        List<TransactionRecord> transactions = databaseConduit.findRecentTransactionRecords();
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Process a money transfer (validates and processes or discards)
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> processTransfer(@RequestBody TransferRequest request) {
        Transaction transaction = new Transaction(
            request.getSenderId(), 
            request.getRecipientId(), 
            request.getAmount()
        );
        
        DatabaseConduit.TransactionResult result = databaseConduit.processKafkaTransaction(transaction);
        
        TransferResponse response = new TransferResponse(
            result.getTransactionRecordId(),
            result.isSuccess() ? "COMPLETED" : "FAILED",
            result.getMessage(),
            java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Note: Direct transaction creation is not supported in the new architecture.
     * All transactions must go through Kafka processing for proper validation.
     */
    @PostMapping
    public ResponseEntity<String> createTransactionNotSupported(@RequestBody CreateTransactionRequest request) {
        return ResponseEntity.badRequest()
            .body("Direct transaction creation is not supported. Use Kafka message processing instead.");
    }
    
    // ==================== REQUEST/RESPONSE DTOs ====================
    
    /**
     * Request object for processing a transfer
     */
    public static class TransferRequest {
        private long senderId;
        private long recipientId;
        private float amount;
        
        // Constructors
        public TransferRequest() {}
        
        public TransferRequest(long senderId, long recipientId, float amount) {
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.amount = amount;
        }
        
        // Getters and setters
        public long getSenderId() { return senderId; }
        public void setSenderId(long senderId) { this.senderId = senderId; }
        
        public long getRecipientId() { return recipientId; }
        public void setRecipientId(long recipientId) { this.recipientId = recipientId; }
        
        public float getAmount() { return amount; }
        public void setAmount(float amount) { this.amount = amount; }
    }
    
    /**
     * Response object for transfer operations
     */
    public static class TransferResponse {
        private Long transactionId;
        private String status;
        private String message;
        private String timestamp;
        
        // Constructors
        public TransferResponse() {}
        
        public TransferResponse(Long transactionId, String status, String message, String timestamp) {
            this.transactionId = transactionId;
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        // Getters and setters
        public Long getTransactionId() { return transactionId; }
        public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * Request object for creating a transaction record
     */
    public static class CreateTransactionRequest {
        private long senderId;
        private long recipientId;
        private float amount;
        
        // Constructors
        public CreateTransactionRequest() {}
        
        public CreateTransactionRequest(long senderId, long recipientId, float amount) {
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.amount = amount;
        }
        
        // Getters and setters
        public long getSenderId() { return senderId; }
        public void setSenderId(long senderId) { this.senderId = senderId; }
        
        public long getRecipientId() { return recipientId; }
        public void setRecipientId(long recipientId) { this.recipientId = recipientId; }
        
        public float getAmount() { return amount; }
        public void setAmount(float amount) { this.amount = amount; }
    }
}
