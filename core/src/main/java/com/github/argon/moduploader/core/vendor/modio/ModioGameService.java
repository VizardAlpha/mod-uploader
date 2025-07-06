package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.vendor.modio.api.ModioGameClient;
import com.github.argon.moduploader.core.vendor.modio.model.ModioGame;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ModioGameService {
    private final ModioGameClient modioGameClient;
    private final ModioMapper modioMapper;

    public Optional<ModioGame> getGame(String apiKey, Long gameId) {
        return modioGameClient.getGame(apiKey, gameId)
            .map(modioMapper::map);
    }


    public List<ModioGame> getGames(String apiKey, @Nullable Long gameId, @Nullable Long submittedBy, @Nullable String name) {
        return modioGameClient.getGames(apiKey, gameId, submittedBy, name)
            .data().stream()
            .map(modioMapper::map)
            .toList();
    }
}
