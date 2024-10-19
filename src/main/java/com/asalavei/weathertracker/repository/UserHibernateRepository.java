package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.entity.User;
import com.asalavei.weathertracker.exception.AlreadyExistsException;
import com.asalavei.weathertracker.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class UserHibernateRepository extends BaseHibernateRepository<User> implements UserRepository {

    @Override
    public User save(User user) {
        try {
            return super.save(user);
        } catch (AlreadyExistsException e) {
            log.warn("username={} is already taken", user.getUsername(), e);
            throw new UserAlreadyExistsException("Username is already taken");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return executeInTransaction(s ->
                Optional.ofNullable(s.createQuery("from User where username = :username", User.class)
                        .setParameter("username", username)
                        .uniqueResult()));
    }
}
