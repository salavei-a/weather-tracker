package com.asalavei.weathertracker.repository;

public interface CrudRepository<T> {
    T save(T entity);
}
