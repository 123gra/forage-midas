package com.jpmc.midascore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Transaction entities.
 * It extends JpaRepository, specifying the entity type (Transaction)
 * and the primary key type (Long).
 */
// @Repository is technically optional here, but good practice to include
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Spring Data JPA automatically provides CRUD methods (save, delete, find, etc.)
    // No additional code is needed inside this interface for basic operations.
}