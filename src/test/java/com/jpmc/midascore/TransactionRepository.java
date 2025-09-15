package com.jpmc.midascore;

// 📦 Importing Spring Data components for repository abstraction
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 🗃️ TransactionRepository provides CRUD operations for TransactionRecord entities.
 * It extends Spring Data's CrudRepository to leverage built-in persistence methods.
 */
@Repository
public interface TransactionRepository extends CrudRepository<TransactionRecord, Long>
{
    // You can add custom query methods here if needed in future
}