package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.config.HibernateConfig;
import com.asalavei.weathertracker.exception.AlreadyExistsException;
import com.asalavei.weathertracker.exception.DatabaseOperationException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.function.Function;

@Slf4j
public class BaseHibernateRepository<T> implements CrudRepository<T> {

    protected static final SessionFactory sessionFactory = HibernateConfig.getSessionFactory();

    @Override
    public T save(T entity) {
        try {
            return executeInTransaction(s -> {
                s.persist(entity);
                return entity;
            });
        } catch (AlreadyExistsException e) {
            String message = getAlreadyExistsMessage(entity);
            log.debug(message, e);
            throw new AlreadyExistsException(message);
        }
    }

    protected <R> R executeInTransaction(Function<Session, R> action) {
        Transaction transaction = null;

        try {
            Session session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            R result = action.apply(session);

            transaction.commit();

            return result;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);

            if (e instanceof ConstraintViolationException) {
                throw new AlreadyExistsException(e);
            }

            log.error("Persistence exception during transaction", e);
            throw new DatabaseOperationException("Database operation failed", e);
        } catch (Exception e) {
            rollbackTransaction(transaction);
            log.error("Unexpected error during transaction", e);
            throw new DatabaseOperationException("Unexpected database operation error", e);
        }
    }

    protected static void rollbackTransaction(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) {
            log.error("Exception while rolling back transaction", e);
        }
    }

    protected String getAlreadyExistsMessage(T entity) {
        return "Entity already exists";
    }
}
