package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserRecord findUserByName(String name) {
        return userRepository.findByName(name);
    }

    public UserRecord findUserById(Long id) {
        Optional<UserRecord> user = userRepository.findById(id);
        return user.orElse(null);
    }
}
