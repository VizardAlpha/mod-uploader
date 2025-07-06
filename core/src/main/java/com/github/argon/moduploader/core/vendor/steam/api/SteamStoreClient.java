package com.github.argon.moduploader.core.vendor.steam.api;

import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamDtoMapper;
import com.github.argon.moduploader.core.vendor.steam.api.dto.SteamSimpleStoreItemDto;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient
@RegisterProvider(SteamDtoMapper.class)
public interface SteamStoreClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/IStoreService/GetAppList/v1")
    List<SteamSimpleStoreItemDto> getApps(
        @QueryParam("key") String apiKey,
        @Nullable @QueryParam("include_games") Boolean includeGames,
        @Nullable @QueryParam("include_dlc") Boolean includeDlc,
        @Nullable @QueryParam("include_software") Boolean includeSoftware,
        @Nullable @QueryParam("include_videos") Boolean includeVideos,
        @Nullable @QueryParam("include_hardware") Boolean includeHardware,
        @Nullable @QueryParam("last_appid") Integer lastAppId,
        @Nullable @QueryParam("max_results") Integer maxResults
    );
}
