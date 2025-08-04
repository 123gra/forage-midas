package com.jpmc.midascore.service.impl;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.BalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceServiceImpl implements BalanceService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceServiceImpl.class);
    private final UserRepository userRepository;

    public BalanceServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Balance getUserBalance(String userId) {
        try {
            Long id = Long.parseLong(userId);
            Optional<UserRecord> userOpt = userRepository.findById(id);

            float balanceAmount = userOpt.map(UserRecord::getBalance).orElse(0.0f);
            return new Balance((float) Math.floor(balanceAmount));
        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", userId);
            return new Balance(0.0f);
        }
    }

}
