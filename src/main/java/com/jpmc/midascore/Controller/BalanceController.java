package com.jpmc.midascore.Controller;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final DatabaseConduit databaseConduit;

    @Autowired
    public BalanceController(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @GetMapping
    public Balance getUserBalance(@RequestParam Long userId) {
        UserRecord user = databaseConduit.queryUser(userId);
        if (user == null) {
            return new Balance(0.0f);
        }
        return new Balance((float) user.getBalance());
    }
}
