package com.jpmc.midascore.controller;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for user-related operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final DatabaseConduit databaseConduit;
    
    public UserController(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }
    
    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<UserRecord>> getAllUsers() {
        List<UserRecord> users = databaseConduit.findAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserRecord> getUserById(@PathVariable long id) {
        Optional<UserRecord> user = databaseConduit.findUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new user
     */
    @PostMapping
    public ResponseEntity<UserRecord> createUser(@RequestBody CreateUserRequest request) {
        UserRecord newUser = new UserRecord(request.getName(), request.getInitialBalance());
        UserRecord savedUser = databaseConduit.saveUser(newUser);
        return ResponseEntity.ok(savedUser);
    }
    
    /**
     * Update user balance
     */
    @PutMapping("/{id}/balance")
    public ResponseEntity<String> updateUserBalance(@PathVariable long id, @RequestBody UpdateBalanceRequest request) {
        boolean updated = databaseConduit.updateUserBalance(id, request.getNewBalance());
        if (updated) {
            return ResponseEntity.ok("Balance updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        Optional<UserRecord> user = databaseConduit.findUserById(id);
        if (user.isPresent()) {
            databaseConduit.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user transaction statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<DatabaseConduit.TransactionStats> getUserStats(@PathVariable long id) {
        Optional<UserRecord> user = databaseConduit.findUserById(id);
        if (user.isPresent()) {
            DatabaseConduit.TransactionStats stats = databaseConduit.getUserTransactionStats(id);
            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ==================== REQUEST DTOs ====================
    
    /**
     * Request object for creating a new user
     */
    public static class CreateUserRequest {
        private String name;
        private float initialBalance;
        
        // Constructors
        public CreateUserRequest() {}
        
        public CreateUserRequest(String name, float initialBalance) {
            this.name = name;
            this.initialBalance = initialBalance;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public float getInitialBalance() { return initialBalance; }
        public void setInitialBalance(float initialBalance) { this.initialBalance = initialBalance; }
    }
    
    /**
     * Request object for updating user balance
     */
    public static class UpdateBalanceRequest {
        private float newBalance;
        
        // Constructors
        public UpdateBalanceRequest() {}
        
        public UpdateBalanceRequest(float newBalance) {
            this.newBalance = newBalance;
        }
        
        // Getters and setters
        public float getNewBalance() { return newBalance; }
        public void setNewBalance(float newBalance) { this.newBalance = newBalance; }
    }
}
