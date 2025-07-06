package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioDtoMapper;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioGameDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioGamesDto;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Optional;

/**
 * For communicating with the mod.io "games" api via REST client.
 */
@Path("games")
@RegisterRestClient
@RegisterProvider(ModioDtoMapper.class) // use custom configured jackson object mapper
public interface ModioGameClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    ModioGamesDto getGames(
        @QueryParam("api_key") String apiKey,
        @Nullable @QueryParam("id") Long gameId,
        @Nullable @QueryParam("submitted_by") Long submittedBy,
        @Nullable @QueryParam("name") String name
    );

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{game-id}")
    Optional<ModioGameDto> getGame(
        @QueryParam("api_key") String apiKey,
        @PathParam("game-id") Long gameId
    );
}
