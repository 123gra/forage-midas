package com.jpmc.midascore;

// 📦 Importing required classes for REST API and data access
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/**
 * 📊 BalanceController exposes an endpoint to retrieve a user's account balance.
 * It interacts with the UserRepository to fetch user data and wraps it in a Balance object.
 */
@RestController
public class BalanceController
{
    // 🧩 Injected repository to access user records
    private final UserRepository userRepository;

    /**
     * 🔧 Constructor-based dependency injection for UserRepository.
     *
     * @param userRepository repository used to fetch user data
     */
    public BalanceController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * 📈 GET endpoint to retrieve balance for a given user ID.
     * Returns a Balance object with the user's current balance,
     * or zero if the user is not found or balance is null.
     *
     * @param userId the ID of the user whose balance is requested
     * @return Balance object containing the user's balance
     */
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId)
    {
        return userRepository.findById(userId)
                .map(user -> new Balance(user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO))
                .orElse(new Balance(BigDecimal.ZERO));
    }
}