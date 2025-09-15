package com.jpmc.midascore.repository;

// 📦 Importing required Spring Data and JPA components
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 🗃️ UserRepository provides CRUD operations and custom queries
 * for the UserRecord entity. It extends Spring Data's CrudRepository
 * to leverage built-in persistence methods.
 */
@Repository
public interface UserRepository extends CrudRepository<UserRecord, Long>
{
    /**
     * 🔍 Finds a UserRecord by its unique ID.
     * Note: CrudRepository already provides findById as Optional,
     * this overrides it to return the entity directly.
     *
     * @param id the user's ID
     * @return the matching UserRecord
     */
    UserRecord findById(long id);

    /**
     * 🔍 Finds a UserRecord by its name.
     * Spring Data automatically implements this based on method naming.
     *
     * @param name the user's name
     * @return the matching UserRecord
     */
    UserRecord findByName(String name);
}