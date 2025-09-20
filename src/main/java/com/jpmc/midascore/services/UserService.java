package com.jpmc.midascore.services;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRecord getUserById(long id) {
        return userRepository.findById(id).
                orElseThrow(() -> new IllegalStateException("User with "+ id + " not found"));
    }

    public Balance getUserBalance(long id) {
        return userRepository.findById(id).
                map(user -> new Balance(user.getBalance())).
                orElse(new Balance(0.0f));
    }
}
