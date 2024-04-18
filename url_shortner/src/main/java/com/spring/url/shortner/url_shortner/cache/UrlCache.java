package com.spring.url.shortner.url_shortner.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class UrlCache {
    private final Map<String, String> cache = new HashMap<>();

    public void put(String longUrl, String shortUrl) {
        cache.put(longUrl, shortUrl);
    }

    public String get(String longUrl) {
        return cache.get(longUrl);
    }

    public boolean contains(String longUrl) {

        return cache.containsKey(longUrl);
    }
}
