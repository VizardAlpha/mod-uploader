package com.github.argon.moduploader.core.vendor.steam;

import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Contexts and Dependency Injection config for Quarkus
 * Here the code for gluing all the classes needed for Steam together lives.
 */
@ApplicationScoped
public class SteamConfiguration {
    public final static String REMOTE_MOD_CACHE = "SteamMod.Remote";
    public final static String STEAM_APP_ID_TXT = "steam_appid.txt";
    public final static Integer DEFAULT_APP_ID = 480;
    public final static String DEFAULT_APP_ID_STRING = "480";


    @Produces
    @Singleton
    public Steam steam(
        IFileService fileService,
        @RestClient SteamStoreClient storeClient,
        SteamMapper steamMapper
    ) {
        return new Steam(fileService, storeClient, steamMapper);
    }
}
