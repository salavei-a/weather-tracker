package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User> {
    Optional<User> findByUsername(String username);
}
