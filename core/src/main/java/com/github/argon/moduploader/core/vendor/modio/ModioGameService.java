package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.vendor.modio.api.ModioGameClient;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioGameDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioGamesDto;
import com.github.argon.moduploader.core.vendor.modio.model.ModioGame;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ModioGameService {
    private final ModioGameClient gameClient;
    private final ModioMapper mapper;

    public Optional<ModioGame> getGame(String apiKey, Long gameId) {
        return gameClient.getGame(apiKey, gameId)
            .map(mapper::map);
    }


    public List<ModioGame> getGames(String apiKey, @Nullable Long gameId, @Nullable Long submittedBy, @Nullable String name) {
        List<ModioGameDto> games = new ArrayList<>();

        int offset = 0;
        int gamesTotal = 0;

        do {
            ModioGamesDto gamesPage = gameClient.getGames(apiKey, gameId, submittedBy, name, offset, null);
            games.addAll(gamesPage.data());

            gamesTotal = gamesPage.resultTotal();
            offset += games.size();
        } while (games.size() < gamesTotal);

        return games.stream()
            .map(mapper::map)
            .toList();
    }
}
