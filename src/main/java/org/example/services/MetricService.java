package org.example.services;

import java.util.concurrent.ConcurrentHashMap;

public class MetricService {
    private ConcurrentHashMap<String, Integer> counters;
    private static MetricService instance;

    private MetricService() {
        this.counters = new ConcurrentHashMap<>();
    }

    public static MetricService getInstance() {
        if (instance == null) {
            synchronized (MetricService.class) {
                if (instance == null) {
                    instance = new MetricService();
                }
            }
        }
        return instance;
    }

    public void incrCounter(String key) {
        this.counters.put(key, this.counters.getOrDefault(key, 0));
    }

    public int getCounter(String key){
        return this.counters.getOrDefault(key, 0);
    }
}
