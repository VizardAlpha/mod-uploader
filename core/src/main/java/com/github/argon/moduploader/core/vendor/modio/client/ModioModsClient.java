package com.github.argon.moduploader.core.vendor.modio.client;

import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioModsDto;
import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioDtoMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("games")
@RegisterRestClient
@RegisterProvider(ModioDtoMapper.class) // use custom configured jackson object mapper
public interface ModioModsClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{game-id}/mods")
    ModioModsDto getMods(
        @QueryParam("api_key") String apiKey,
        @PathParam("game-id") Long gameId
    );

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{game-id}/mods")
    ModioModsDto getModsByUser(
        @QueryParam("api_key") String apiKey,
        @PathParam("game-id") Long gameId,
        @QueryParam("submitted_by") Long userId
    );
}
