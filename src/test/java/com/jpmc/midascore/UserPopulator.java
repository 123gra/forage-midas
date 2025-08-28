package com.jpmc.midascore;

// 📦 Importing required components for data loading and persistence
import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 👥 UserPopulator reads user data from a file and persists it to the database.
 * It transforms each line into a UserRecord and delegates saving to DatabaseConduit.
 */
@Component
public class UserPopulator
{
    // 📂 Loads user data from classpath files
    @Autowired
    private FileLoader fileLoader;

    // 💾 Handles persistence of UserRecord entities
    @Autowired
    private DatabaseConduit databaseConduit;

    /**
     * 🧾 Reads user data from a file and saves each record to the database.
     * Expected format per line: "name, balance"
     */
    public void populate()
    {
        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");

        for (String userLine : userLines)
        {
            String[] userData = userLine.split(", ");
            UserRecord user = new UserRecord(
                    userData[0],
                    new java.math.BigDecimal(userData[1])
            );
            databaseConduit.save(user);
        }
    }
}