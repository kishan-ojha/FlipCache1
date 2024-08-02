package org.example.interfaces;

import org.example.exceptions.DataStoreException;

import java.util.Optional;

public interface DataStore<K, V> {
    void set(K key, V value);
    void delete(K entity) throws DataStoreException;
    Optional<V> get(K entity);
}
