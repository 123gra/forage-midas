package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    @Autowired
    private UserRepository userRepository;

    public Balance getBalanceByUserId(Long userId) {
        UserRecord user = userRepository.findById(userId.longValue());
        if (user != null) {
            return new Balance(user.getBalance());
        } else {
            // Return balance of 0 if user doesn't exist
            return new Balance(0.0f);
        }
    }
}
