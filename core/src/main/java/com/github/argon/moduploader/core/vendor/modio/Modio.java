package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.AuthException;
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

/**
 * For interacting with mod.io
 */
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

    /**
     * Fetches all published mods by the given bearer token (current logged-in user)
     */
    public List<ModioMod.Remote> getPublishedMods(String apiKey, Long gameId, BearerToken bearerToken) {
        ModioUser modioUser = userService().getUser(bearerToken);
        Long userId = modioUser.id();

        return modService().getUserMods(apiKey, gameId, userId);
    }

    /**
     * Fetches all mods from a game.
     * Results will be cached by the gameId.
     */
    @CacheResult(cacheName = CACHE_NAME)
    public List<ModioMod.Remote> getMods(String apiKey, @CacheKey Long gameId) {
        return modService().getMods(apiKey, gameId, null, null, null, null);
    }

    /**
     * Upload a mod with file data to mod.io
     * Will create a new mod when {@link ModioMod.Local#id()} is null.
     *
     * @throws VendorException when packaging or uploading the file data fails
     * @throws AuthException when there is no bearer token
     */
    public ModioMod.Remote upload(Long gameId, ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        return modService().upload(gameId, mod, version, changelog);
    }

    /**
     * Will archive the mod and make it inaccessible for others.
     * The mod itself can only be deleted via the mod.io web ui.
     *
     * @return whether archivation was successful
     */
    public boolean archiveMod(Long gameId, Long modId) {
        try (Response response = modService().archive(gameId, modId)) {

            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes entries with given gameId from cache
     */
    @Override
    @CacheInvalidate(cacheName = CACHE_NAME)
    public void invalidate(@CacheKey Long gameId) {}

    /**
     * Removes all entries from cache
     */
    @Override
    @CacheInvalidateAll(cacheName = CACHE_NAME)
    public void invalidateAll() {}
}
