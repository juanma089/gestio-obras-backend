package com.gestion_obras.services;

import java.util.List;
import java.util.Optional;

public interface GenericService<T> {

    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T entity);

    boolean delete(Long id);

}
