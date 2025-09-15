package com.jpmc.midascore;

// 📦 Importing required Kafka and Spring components
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * 📥 TransactionListener consumes messages from Kafka and delegates them
 * to the TransactionProcessor for business logic execution.
 */
@Component
public class TransactionListener
{
    // 🔧 Injected processor to handle parsed transactions
    private final TransactionProcessor transactionProcessor;

    /**
     * 🛠️ Constructor-based injection of TransactionProcessor.
     *
     * @param transactionProcessor the service that processes incoming transactions
     */
    public TransactionListener(TransactionProcessor transactionProcessor)
    {
        this.transactionProcessor = transactionProcessor;
    }

    /**
     * 🎧 Kafka listener method that receives raw transaction messages.
     * Parses the message and forwards it to the processor.
     *
     * @param message comma-separated transaction string (e.g., "1, 2, 100.00")
     */
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(String message)
    {
        try
        {
            String[] parts = message.split(",");
            long senderId = Long.parseLong(parts[0].trim());
            long recipientId = Long.parseLong(parts[1].trim());
            BigDecimal amount = new BigDecimal(parts[2].trim());

            Transaction transaction = new Transaction(senderId, recipientId, amount);
            transactionProcessor.process(transaction);

        }
        catch (Exception e)
        {
            System.err.println("❌ Failed to process message: " + message);
            e.printStackTrace();
        }
    }
}