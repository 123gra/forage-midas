package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BalanceQuerier {
    
    @Autowired
    private UserRepository userRepository;
    
    public Balance query(Long userId) {
        Optional<UserRecord> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return new Balance(userOpt.get().getBalance());
        }
        return new Balance(0.0f);
    }
    
    public UserRecord queryUserById(Long userId) {
        Optional<UserRecord> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null);
    }
    
    public UserRecord queryUserByName(String name) {
        // Since we don't have a findByName method, we'll need to get all users and find by name
        // This is not efficient but works for this use case
        Iterable<UserRecord> allUsers = userRepository.findAll();
        for (UserRecord user : allUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }
}
