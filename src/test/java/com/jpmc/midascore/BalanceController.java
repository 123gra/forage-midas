package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

@RestController
public class BalanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private final UserRepository userRepository;
    
    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Task 5: GET /balance endpoint
     * Returns the balance for a given userId
     * If user does not exist, returns balance of 0
     */
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam long userId) {
        logger.info("🔍 GET /balance called - userId: {}", userId);
        
        UserRecord user = userRepository.findById(userId);
        
        if (user != null) {
            logger.info("✅ User found: {} - balance: {}", user.getName(), user.getBalance());
            return new Balance(userId, user.getBalance());
        } else {
            logger.warn("⚠️ User {} not found - returning balance 0", userId);
            return new Balance(userId, 0.0f);
        }
    }
    
    /**
     * Task 4: POST /incentive endpoint
     * Calculates 2% incentive for a transaction
     */
    @PostMapping("/incentive")
    public Incentive calculateIncentive(@RequestBody Transaction transaction) {
        logger.info("💰 POST /incentive called - Transaction: senderId={}, recipientId={}, amount={}", 
                transaction.getSenderId(), 
                transaction.getRecipientId(), 
                transaction.getAmount());
        
        // Calculate 2% incentive
        float incentiveAmount = transaction.getAmount() * 0.02f;
        
        logger.info("✅ Calculated incentive: {} (2% of {})", incentiveAmount, transaction.getAmount());
        
        return new Incentive(incentiveAmount);
    }
}