package com.github.argon.moduploader.core.vendor;

import io.quarkus.cache.CacheKey;

public interface Cachable<K> {
    void invalidate(@CacheKey K cacheKey);
    void invalidateAll();
}
