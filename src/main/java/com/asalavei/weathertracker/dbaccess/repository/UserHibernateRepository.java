package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserHibernateRepository extends BaseHibernateRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        return executeInTransaction(s -> {
            User user = s.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(user);
        });
    }
}
