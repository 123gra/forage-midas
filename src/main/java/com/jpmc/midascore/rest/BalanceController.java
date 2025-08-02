package com.jpmc.midascore.rest;

import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    private static final Balance ZERO_BALANCE = new Balance(0);

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	@GetMapping("/balance")
	public Balance greeting(final Long userId) {
        Optional<UserRecord> userRecord = userRepository.findById(userId);
        if (userRecord.isPresent()) {
            return new Balance(userRecord.get().getBalance());
        } else {
            return ZERO_BALANCE;
        }
	}
   , 
}
