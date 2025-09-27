package com.jpmc.midascore.consumer;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionProcessor;
import com.jpmc.midascore.service.TransactionProcessor.ProcessingResult;
import com.jpmc.midascore.service.TransactionMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for processing transactions
 * Listens to the configured transaction topic and validates/processes incoming transactions
 */
@Component
public class KafkaTransactionConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionConsumer.class);
    
    private final TransactionProcessor transactionProcessor;
    private final TransactionMonitoringService monitoringService;
    
    public KafkaTransactionConsumer(TransactionProcessor transactionProcessor, 
                                   TransactionMonitoringService monitoringService) {
        this.transactionProcessor = transactionProcessor;
        this.monitoringService = monitoringService;
    }
    
    /**
     * Listens to the transaction topic and processes incoming transactions
     * 
     * @param transaction The transaction received from Kafka
     * @param acknowledgment Kafka acknowledgment for manual commit
     * @param partition The partition the message came from
     * @param offset The offset of the message
     * @param topic The topic the message came from
     */
    @KafkaListener(topics = "${general.kafka-topic}")
    public void consumeTransaction(
            @Payload Transaction transaction,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        logger.info("Received transaction from Kafka - Topic: {}, Partition: {}, Offset: {}, Transaction: {}", 
                   topic, partition, offset, transaction);
        
        try {
            // Process the transaction
            ProcessingResult result = transactionProcessor.processTransaction(transaction);
            
            // Asynchronously log detailed processing result
            monitoringService.logProcessingResult(result, transaction);
            
            // Log the result in the consumer
            switch (result.getStatus()) {
                case SUCCESS:
                    logger.info("Transaction processed successfully - ID: {}, Message: {}", 
                               result.getTransactionId(), result.getMessage());
                    break;
                    
                case VALIDATION_FAILED:
                    logger.warn("Transaction validation failed - ID: {}, Message: {}", 
                               result.getTransactionId(), result.getMessage());
                    break;
                    
                case PROCESSING_FAILED:
                    logger.error("Transaction processing failed - ID: {}, Message: {}", 
                                result.getTransactionId(), result.getMessage());
                    break;
                    
                case ERROR:
                    logger.error("Transaction processing error - ID: {}, Message: {}", 
                                result.getTransactionId(), result.getMessage());
                    break;
            }
            
            // Always acknowledge the message, even if processing failed
            // Failed transactions are recorded in the database with FAILED status
            acknowledgment.acknowledge();
            
            logger.debug("Kafka message acknowledged for transaction at offset: {}", offset);
            
        } catch (Exception e) {
            logger.error("Unexpected error consuming transaction from Kafka - Topic: {}, Partition: {}, Offset: {}, Transaction: {}", 
                        topic, partition, offset, transaction, e);
            
            // Still acknowledge to prevent infinite reprocessing of poison messages
            // The error is logged and can be monitored/alerted on
            acknowledgment.acknowledge();
        }
    }
    
}
