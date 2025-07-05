package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.Cachable;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Modio implements Cachable<Long> {
    private final ModioStoreService storeService;
    private final ModioUserService userService;
    private final ModioAuthService authService;

    public final static String CACHE_NAME = "ModioMod.Remote";

    public ModioStoreService storeService() {
        return storeService;
    }

    public ModioUserService userService() {
        return userService;
    }

    public ModioAuthService authService() {
        return authService;
    }

    public List<ModioMod.Remote> getPublishedMods(String apiKey, Long gameId, BearerToken bearerToken) {
        ModioUser modioUser = userService().getUser(bearerToken);
        Long userId = modioUser.id();

        return storeService().getUserMods(apiKey, gameId, userId);
    }

    public List<ModioMod.Remote> searchMods(
        String apiKey,
        Long gameId,
        @Nullable Long submittedBy,
        @Nullable String submittedByDisplayName,
        @Nullable String modName,
        @Nullable List<String> tags
    ) {
        return storeService().searchMods(apiKey, gameId, submittedBy, submittedByDisplayName, modName, tags);
    }

    @CacheResult(cacheName = CACHE_NAME)
    public List<ModioMod.Remote> getMods(String apiKey, @CacheKey Long gameId) {
        return storeService().searchMods(apiKey, gameId, null, null, null, null);
    }

    public ModioMod.Remote upload(Long gameId, ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        return storeService().upload(gameId, mod, version, changelog);
    }

    @Override
    @CacheInvalidate(cacheName = CACHE_NAME)
    public void invalidate(@CacheKey Long cacheKey) {}

    @Override
    @CacheInvalidateAll(cacheName = CACHE_NAME)
    public void invalidateAll() {}
}
