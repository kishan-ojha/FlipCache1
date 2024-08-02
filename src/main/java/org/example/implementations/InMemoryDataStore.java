package org.example.implementations;

import org.example.exceptions.DataStoreException;
import org.example.interfaces.DataStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryDataStore<K, V> implements DataStore<K, V> {
    private final ConcurrentHashMap<K, V> keyValueMap;
    private Lock deleteLock;
    public InMemoryDataStore(){
        this.keyValueMap = new ConcurrentHashMap<>();
        this.deleteLock = new ReentrantLock(true);
    }

    @Override
    public void set(K key, V value) {
        this.keyValueMap.put(key, value);
    }

    @Override
    public void delete(K key) throws DataStoreException {
        this.deleteLock.lock();
        if(this.keyValueMap.containsKey(key)){
            this.keyValueMap.remove(key);
            return;
        }

        this.deleteLock.unlock();
        throw new DataStoreException("key does not exists to be deleted");
    }

    @Override
    public Optional<V> get(K key) {
        if(this.keyValueMap.containsKey(key)){
            return Optional.of(this.keyValueMap.get(key));
        }

        return Optional.empty();
    }
}
