package org.example.services;

import org.example.exceptions.CacheException;
import org.example.exceptions.DataStoreException;
import org.example.exceptions.EvictionStrategyException;
import org.example.interfaces.DataStore;
import org.example.interfaces.EvictionStrategy;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.models.Constants.CACHE_HIT;
import static org.example.models.Constants.CACHE_MISS;


public class CacheService<K, V> {
    private final DataStore<K, V> dataStore;
    private final EvictionStrategy<K> evictionStrategy;
    private final int capacity;
    private int size;
    private static CacheService instance;
    private final Lock lock;
    private final MetricService metricService;

    private CacheService(DataStore<K, V> dataStore, EvictionStrategy<K> evictionStrategy, MetricService metricService,
                         int capacity) {
        this.dataStore = dataStore;
        this.evictionStrategy = evictionStrategy;
        this.capacity = capacity;
        this.size = 0;
        this.lock = new ReentrantLock();
        this.metricService = metricService;
    }

    public static CacheService getInstance(DataStore dataStore, EvictionStrategy evictionStrategy,
                                           MetricService metricService, int capacity) {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService(dataStore, evictionStrategy, metricService, capacity);
                }
            }
        }
        return instance;
    }

    public void set(K key, V value) throws CacheException {
        lock.lock();
        try {
            Optional<V> currentValue = this.dataStore.get(key);
            if (currentValue.isPresent()) {
                update(key, value);
                lock.unlock();
                return;
            }

            this.dataStore.set(key, value);
            this.evictionStrategy.addKeyToPool(key);
            this.size++;
            System.out.println("SET: key :" + key + ", value: " + value);
            checkForEviction();
            lock.unlock();
        } catch (Exception ex) {
            lock.unlock();
            throw new CacheException("Error while set key in cache:" + ex.getMessage());
        }

    }

    public Optional<V> get(K key) {
        Optional<V> result = this.dataStore.get(key);
        if (result.isPresent()) {
            metricService.incrCounter(CACHE_HIT);
            System.out.println("GET: key :" + key + ", value: " + result.get());
            return result;
        }
        System.out.println("GET: key :" + key + ", value not found");
        metricService.incrCounter(CACHE_MISS);
        return result;
    }

    public void delete(K key) throws CacheException {
        lock.lock();
        try {
            this.dataStore.delete(key);
            this.evictionStrategy.removeKeyFromPool(key);
            System.out.println("DELETE: key :" + key);
            this.size--;
            lock.unlock();
        } catch (Exception ex) {
            lock.unlock();
            throw new CacheException("Error while deleting key from cache:" + ex.getMessage());
        }
    }

    private void checkForEviction() throws DataStoreException, EvictionStrategyException {
        if (size <= capacity) {
            return;
        }

        // evict
        K key = this.evictionStrategy.evictedKey();
        this.dataStore.delete(key);
        this.size--;
        System.out.println("EVICTED: key :" + key);
    }

    private void update(K key, V value) throws EvictionStrategyException {
        this.dataStore.set(key, value);
        this.evictionStrategy.removeKeyFromPool(key);
        this.evictionStrategy.addKeyToPool(key);
        System.out.println("UPDATE: key :" + key + ", value: " + value);
    }
}
