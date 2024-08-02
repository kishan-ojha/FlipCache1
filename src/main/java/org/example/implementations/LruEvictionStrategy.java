package org.example.implementations;

import org.example.interfaces.EvictionStrategy;

public class LruEvictionStrategy<T> implements EvictionStrategy<T> {
    @Override
    public void addKeyToPool(T key) {

    }

    @Override
    public T evictedKey() {
        return null;
    }

    @Override
    public void removeKeyFromPool(T key) {

    }
}
