package com.asalavei.weathertracker.auth.user;

import com.asalavei.weathertracker.common.BaseHibernateRepository;
import com.asalavei.weathertracker.exception.AlreadyExistsException;
import com.asalavei.weathertracker.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class UserHibernateRepository extends BaseHibernateRepository<User> implements UserRepository {

    public UserHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public User save(User user) {
        try {
            return super.save(user);
        } catch (AlreadyExistsException e) {
            log.debug("username={} is already taken", user.getUsername(), e);
            throw new UserAlreadyExistsException("Username is already taken");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return executeInTransaction(s ->
                s.createQuery("from User where username = :username", User.class)
                        .setParameter("username", username)
                        .uniqueResultOptional());
    }
}
