package com.asalavei.weathertracker.repository;

import com.asalavei.weathertracker.exception.AlreadyExistsException;
import com.asalavei.weathertracker.exception.DatabaseOperationException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHibernateRepository<T> implements CrudRepository<T> {

    private final SessionFactory sessionFactory;

    @Override
    public T save(T entity) {
        return executeInTransaction(s -> {
            s.persist(entity);
            return entity;
        });
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
                throw new AlreadyExistsException("Entity already exists", e);
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
}
