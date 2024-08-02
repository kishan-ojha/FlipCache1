package org.example.implementations;

import org.example.exceptions.EvictionStrategyException;
import org.example.interfaces.EvictionStrategy;
import org.example.models.TimeBasedKeyNode;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeBasedEvictionStrategy<T> implements EvictionStrategy<T> {
    private final PriorityBlockingQueue<TimeBasedKeyNode<T>> queue;
    private final ConcurrentHashMap<T, TimeBasedKeyNode<T>> keyNodeMap;
    private final Lock lock;

    public TimeBasedEvictionStrategy() {
        this.queue = new PriorityBlockingQueue<>(11, (n1, n2) -> {
            if (n1.getLocalDateTime().isBefore(n2.getLocalDateTime())) {
                return -1;
            }

            if (n1.getLocalDateTime().isEqual(n2.getLocalDateTime())) {
                return 0;
            }

            return 1;
        });
        this.keyNodeMap = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock(true);
    }

    @Override
    public void addKeyToPool(T key) throws EvictionStrategyException {
        lock.lock();
        try {
            LocalDateTime time = LocalDateTime.now();
            TimeBasedKeyNode<T> node = new TimeBasedKeyNode<>(key, time);
            this.queue.add(node);
            this.keyNodeMap.put(key, node);
            System.out.println("AddKeyToPool: key :" + key);
            lock.unlock();
        } catch (Exception ex) {
            lock.unlock();
            throw new EvictionStrategyException("Error while adding key to the pool:" + ex.getMessage());
        }
    }

    @Override
    public T evictedKey() throws EvictionStrategyException {
        lock.lock();
        try {
            TimeBasedKeyNode<T> keyNode = this.queue.remove();
            System.out.println("GetKeyToBeEvicted: key :" + keyNode.getKey());
            this.keyNodeMap.remove(keyNode.getKey());
            lock.unlock();
            return keyNode.getKey();
        } catch (Exception ex) {
            lock.unlock();
            throw new EvictionStrategyException("Error while evicting key from the pool:" + ex.getMessage());
        }
    }

    @Override
    public void removeKeyFromPool(T key) throws EvictionStrategyException {
        lock.lock();
        try {
            if (keyNodeMap.containsKey(key)) {
                TimeBasedKeyNode<T> keyNode = keyNodeMap.get(key);
                System.out.println("RemoveKeyFromPool: key :" + keyNode.getKey());
                this.queue.remove(keyNode);
                this.keyNodeMap.remove(key);
            }
            lock.unlock();
        } catch (Exception ex) {
            lock.unlock();
            throw new EvictionStrategyException("Error while removing key from the pool:" + ex.getMessage());
        }

    }
}
