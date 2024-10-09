package com.asalavei.weathertracker.dbaccess.repository;

import com.asalavei.weathertracker.dbaccess.config.HibernateConfig;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

@Slf4j
public class BaseHibernateRepository<T> implements CrudRepository<T> {

    protected static final SessionFactory sessionFactory = HibernateConfig.getSessionFactory();

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
        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw e;
            // TODO: handle custom exception
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
