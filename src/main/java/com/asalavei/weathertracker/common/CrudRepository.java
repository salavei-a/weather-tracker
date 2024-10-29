package com.asalavei.weathertracker.common;

public interface CrudRepository<T> {
    T save(T entity);
}
