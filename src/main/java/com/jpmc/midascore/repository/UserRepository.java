package com.jpmc.midascore.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpmc.midascore.entity.UserRecord;

public interface UserRepository extends CrudRepository<UserRecord, Long> {
    UserRecord findById(long id);
    
    // Add this method for Task 3 - to find waldorf by name
    UserRecord findByName(String name);
}