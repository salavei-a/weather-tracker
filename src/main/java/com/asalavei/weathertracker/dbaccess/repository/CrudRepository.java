package com.asalavei.weathertracker.dbaccess.repository;

public interface CrudRepository<T> {
    T save(T entity);
}
