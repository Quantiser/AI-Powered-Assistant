package com.quantizer.aiprogrammingassistant.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.quantizer.aiprogrammingassistant.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}