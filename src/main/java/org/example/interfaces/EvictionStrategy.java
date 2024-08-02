package org.example.interfaces;

import org.example.exceptions.EvictionStrategyException;

public interface EvictionStrategy<T> {
    void addKeyToPool(T key) throws EvictionStrategyException;

    T evictedKey() throws EvictionStrategyException;

    void removeKeyFromPool(T key) throws EvictionStrategyException;
}
