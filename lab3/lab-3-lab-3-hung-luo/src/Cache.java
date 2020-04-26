package com.dslab2;
import java.util.HashMap;
import java.util.Map;

public class Cache {
    public Map<Integer, String> results = new HashMap<>();

    public Cache() {

    }

    public boolean check(int itemNumber) {
        return results.containsKey(itemNumber);
    }

    public String get(int itemNumber) {
        return results.get(itemNumber);
    }

    public void put(int itemNumber, String result) {
        results.put(itemNumber, result);
    }

    public void remove(int itemNumber) {
        results.remove(itemNumber);
    }
}

