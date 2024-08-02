package org.example;

import org.example.implementations.InMemoryDataStore;
import org.example.implementations.TimeBasedEvictionStrategy;
import org.example.interfaces.DataStore;
import org.example.interfaces.EvictionStrategy;
import org.example.services.CacheService;
import org.example.services.MetricAnalyzer;
import org.example.services.MetricService;

import java.util.Optional;

public class Main {
    /*
     *
        Requirements are:
Cache should be bounded in the number of keys.
Design it in a way so that it is easy to add the following to the library
time bound eviction policy - which decides how to evacuate old data to make room for new data based on time.
data bound eviction policy - which decides how to evacuate old data to make room for new data based on new data.
Data store - where it keeps key-value pairs.
Users of the cache should be able to specify data store & eviction policy at initialization.
Provide one implementation of data store and eviction strategy.
The solution should be able to maintain the concurrent requests.
Provide metrics around eviction strategy. One such metric possible is the hit ratio defined as: Number of key
* references that hit in the cache / total number of memory references.

     * */
    public static void main(String[] args) throws Exception {
        EvictionStrategy<String> timeBasedEvictionStrategy = new TimeBasedEvictionStrategy<>();
        DataStore<String, String> dataStore = new InMemoryDataStore<>();
        MetricService metricService = MetricService.getInstance();

        MetricAnalyzer metricAnalyzer = MetricAnalyzer.getInstance(metricService);
        CacheService<String, String> cacheService = CacheService.getInstance(dataStore, timeBasedEvictionStrategy, metricService, 2);
        cacheService.set("key1", "value1");
        cacheService.set("key2", "value2");

        Optional<String> value = cacheService.get("key1");
        if (value.isPresent()) {
            System.out.println("Value found for key1 : " + value);
        } else {
            System.out.println("Value not found  for key1");
        }

        cacheService.set("key3", "value3");

        // should not get value for key1
        value = cacheService.get("key1");
        if (value.isPresent()) {
            System.out.println("Value found for key1 : " + value);
        } else {
            System.out.println("Value not found  for key1");
        }
    }
}