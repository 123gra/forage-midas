package com.jpmc.midascore.repository; // Or your package

// Import the ACTUAL database entity class
import com.jpmc.midascore.model.Transaction; // Make sure this path is correct

// Use JpaRepository (preferred) or CrudRepository
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

@Repository
// Name should match the field in TransactionService (usually TransactionRepository)
// Generic types MUST be <EntityClass, IdType> -> <Transaction, Long>
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // This interface should usually be empty initially.
    // Spring Data JPA automatically provides .save(), .findById(), etc.
}