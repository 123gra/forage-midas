package com.jpmc.midascore.controllers;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

//    @GetMapping("/balance")
//    public float getUserBalanceById(@RequestParam long userId) {
//        if (userService.getUserById(userId).getBalance() == 0) {
//            return 0.0f;
//        }
//        return userService.getUserById(userId).getBalance();
//    }

    @GetMapping("/balance")
    public Balance getUserBalance(@RequestParam long userId) {
        return userService.getUserBalance(userId);
    }
}
