package com.jpmc.midascore.component;

// 🔗 Importing required classes and annotations
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 📦 DatabaseConduit acts as a bridge between business logic and data persistence.
 * It encapsulates database operations related to UserRecord entities.
 */
@Component
public class DatabaseConduit
{
    // 🧩 Dependency Injection: Injecting UserRepository to interact with the database
    private final UserRepository userRepository;

    /**
     * 🔧 Constructor-based injection of UserRepository.
     * Promotes immutability and easier testing.
     *
     * @param userRepository the repository used for UserRecord persistence
     */
    public DatabaseConduit(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * 💾 Saves a UserRecord entity to the database.
     * The @Transactional annotation ensures atomicity and rollback on failure.
     *
     * @param userRecord the user data to be persisted
     */
    @Transactional
    public void save(UserRecord userRecord)
    {
        userRepository.save(userRecord);
    }
}