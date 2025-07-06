package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.*;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

/**
 * For communicating with the mod.io "mods" api via REST client.
 */
@Path("games")
@RegisterRestClient
@RegisterProvider(ModioDtoMapper.class) // use custom configured jackson object mapper
public interface ModioModsClient {

    /**
     * For fetching information about a single mod
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{game-id}/mods/{mod-id}")
    Optional<ModioModDto> getMod(
        @QueryParam("api_key") String apiKey,
        @PathParam("game-id") Long gameId,
        @PathParam("mod-id") Long modId
    );

    /**
     * For fetching multiple mods filtered via given parameters.
     * Will return all mods when all optional parameters are null.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{game-id}/mods")
    ModioModsDto getMods(
        @QueryParam("api_key") String apiKey,
        @PathParam("game-id") Long gameId,
        @Nullable @QueryParam("submitted_by") Long submittedBy,
        @Nullable @QueryParam("submitted_by_display_name") String submittedByDisplayName,
        @Nullable @QueryParam("name") String name,
        @Nullable @QueryParam("tags") List<String> tags,
        @Nullable @QueryParam("_offset") Integer offset,
        @Nullable @QueryParam("_limit") Integer limit
    );

    /**
     * For create a new mod on mod.io
     *
     * @param bearerToken from the author
     * @param gameId to publish the mod for
     * @param addModDto information about the new mod
     * @return created mod
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{game-id}/mods")
    ModioModDto addMod(
        @HeaderParam("Authorization") String bearerToken,
        @PathParam("game-id") Long gameId,
        ModioAddModDto addModDto
    );

    /**
     * For adding files to an existing mod on mod.io
     *
     * @param bearerToken from the author
     * @param gameId of the game you are modding
     * @param modId of the mod to add the files for
     * @param addModFileDto information and data to upload
     * @return uploaded mod file information
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{game-id}/mods/{mod-id}/files")
    ModioModFileDto addModFile(
        @HeaderParam("Authorization") String bearerToken,
        @PathParam("game-id") Long gameId,
        @PathParam("mod-id") Long modId,
        ModioAddModFileDto addModFileDto
    );

    /**
     * Updates a mod
     *
     * @param bearerToken from the author
     * @param gameId to update the mod for
     * @param modId of the mod to update
     * @param editModDto updated mod information
     * @return updated mod
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{game-id}/mods/{mod-id}")
    ModioModDto editMod(
        @HeaderParam("Authorization") String bearerToken,
        @PathParam("game-id") Long gameId,
        @PathParam("mod-id") Long modId,
        ModioEditModDto editModDto
    );

    /**
     * Archives a mod
     *
     * @param bearerToken from the author
     * @param gameId to archive the mod from
     * @param modId of the mod to delete
     * @return a response with 204 No Content
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // seems ignored when there's no content / body
    @Path("/{game-id}/mods/{mod-id}")
    Response archiveMod(
        @HeaderParam("Content-Type") String contentType, // force the header to be set
        @HeaderParam("Authorization") String bearerToken,
        @PathParam("game-id") Long gameId,
        @PathParam("mod-id") Long modId
    );

    default Response archiveMod(
         String bearerToken,
         Long gameId,
         Long modId
    ) {
        return archiveMod(MediaType.APPLICATION_FORM_URLENCODED, bearerToken, gameId, modId);
    }
}
