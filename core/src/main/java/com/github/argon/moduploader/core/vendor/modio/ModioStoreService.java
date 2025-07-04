package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.vendor.modio.client.ModioModsClient;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;

import java.util.List;

public class ModioStoreService {

    private final String apiKey;
    private final Long gameId;
    private final ModioModsClient modioClient;

    public ModioStoreService(String apiKey, Long gameId, ModioModsClient modioModsClient) {
        this.apiKey = apiKey;
        this.gameId = gameId;
        this.modioClient = modioModsClient;
    }

    public List<ModioMod.Remote> fetchPublishedMods(Long userId) {
        // todo paging
        return modioClient.getModsByUser(apiKey, gameId, userId)
            .data().stream()
            .map(ModioMapper::map)
            .toList();
    }
}
