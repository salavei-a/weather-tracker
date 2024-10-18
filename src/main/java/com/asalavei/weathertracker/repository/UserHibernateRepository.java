package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserHibernateRepository extends BaseHibernateRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        return executeInTransaction(s ->
                Optional.ofNullable(s.createQuery("from User where username = :username", User.class)
                        .setParameter("username", username)
                        .uniqueResult()));
    }

    @Override
    protected String getAlreadyExistsMessage(User user) {
        return "Username '" + user.getUsername() + "' is already taken";
    }
}
