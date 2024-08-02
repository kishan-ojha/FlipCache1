package org.example.services;

import static org.example.models.Constants.CACHE_HIT;
import static org.example.models.Constants.CACHE_MISS;

public class MetricAnalyzer {
    private MetricService metricService;
    private static MetricAnalyzer instance;

    private MetricAnalyzer(MetricService metricService) {
        this.metricService = metricService;
    }

    public static MetricAnalyzer getInstance(MetricService metricService) {
        if (instance == null) {
            synchronized (MetricService.class) {
                if (instance == null) {
                    instance = new MetricAnalyzer(metricService);
                }
            }
        }
        return instance;
    }

    public double getCacheHitRatio() {
        int cacheHit = this.metricService.getCounter(CACHE_HIT);
        int cacheMiss = this.metricService.getCounter(CACHE_MISS);
        if (cacheMiss + cacheHit == 0) {
            return 0;
        }

        return (double) cacheHit / (cacheMiss + cacheHit);
    }
}
