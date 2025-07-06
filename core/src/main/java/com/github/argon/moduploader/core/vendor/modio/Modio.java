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
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Modio implements Cachable<Long> {
    private final ModioModService modService;
    private final ModioUserService userService;
    private final ModioAuthService authService;
    private final ModioGameService gameService;

    public final static String CACHE_NAME = "ModioMod.Remote";

    public ModioModService modService() {
        return modService;
    }

    public ModioUserService userService() {
        return userService;
    }

    public ModioAuthService authService() {
        return authService;
    }

    public ModioGameService gameService() {
        return gameService;
    }

    public List<ModioMod.Remote> getPublishedMods(String apiKey, Long gameId, BearerToken bearerToken) {
        ModioUser modioUser = userService().getUser(bearerToken);
        Long userId = modioUser.id();

        return modService().getUserMods(apiKey, gameId, userId);
    }

    @CacheResult(cacheName = CACHE_NAME)
    public List<ModioMod.Remote> getMods(String apiKey, @CacheKey Long gameId) {
        return modService().getMods(apiKey, gameId, null, null, null, null);
    }

    public ModioMod.Remote upload(Long gameId, ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        return modService().upload(gameId, mod, version, changelog);
    }

    public boolean deleteMod(Long gameId, Long modId) {
        try (Response response = modService().delete(gameId, modId)) {

            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                return true;
            }
        }

        return false;
    }

    @Override
    @CacheInvalidate(cacheName = CACHE_NAME)
    public void invalidate(@CacheKey Long cacheKey) {}

    @Override
    @CacheInvalidateAll(cacheName = CACHE_NAME)
    public void invalidateAll() {}
}
