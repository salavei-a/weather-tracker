package com.asalavei.weathertracker.auth.user;

import com.asalavei.weathertracker.common.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User> {
    Optional<User> findByUsername(String username);
}
