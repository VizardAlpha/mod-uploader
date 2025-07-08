package com.github.argon.moduploader.core.vendor.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.github.argon.moduploader.core.Awaitable;
import com.github.argon.moduploader.core.Initializable;
import com.github.argon.moduploader.core.InitializeException;
import com.github.argon.moduploader.core.NotInitializedException;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreClient;
import com.github.argon.moduploader.core.vendor.steam.api.SteamUserHandler;
import com.github.argon.moduploader.core.vendor.steam.api.SteamWorkshopHandler;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

/**
 * For interacting with Steam.
 * Contains all logic to initialize the native {@link SteamAPI}.
 */
@Slf4j
public class Steam implements Closeable, Awaitable, Runnable, Initializable<Integer> {


    @Getter
    private final String steamAppIdTxt;
    private SteamWorkshopService workshop;
    private Integer appId = SteamConfiguration.DEFAULT_APP_ID;
    private final IFileService fileService;
    private final SteamStoreClient storeClient;
    private final SteamMapper mapper;

    public Steam(IFileService fileService, SteamStoreClient storeClient, SteamMapper mapper) {
        this(SteamConfiguration.STEAM_APP_ID_TXT, fileService, storeClient, mapper);
    }



    /**
     * @param fileService for writing the steamAppIdTxt
     * @param steamAppIdTxt name of the steam appid txt file, which needs to be present next to this app
     */
    public Steam(
        String steamAppIdTxt,
        IFileService fileService,
        SteamStoreClient storeClient,
        SteamMapper mapper
    ) {
        this.steamAppIdTxt = steamAppIdTxt;
        this.fileService = fileService;
        this.mapper = mapper;
        this.storeClient = storeClient;
    }

    public SteamWorkshopService workshop() {
        if (workshop == null) {
            throw new NotInitializedException("You have to call Steam.init(appId) first");
        }

        return workshop;
    }

    public Integer appId() {
        if (appId == null) {
            throw new NotInitializedException("You have to call Steam.init(appId) first");
        }

        return appId;
    }

    /**
     * Will throw away all open steam handlers if there are any
     */
    @Override
    public void close() {
        if (workshop != null) {
            workshop.close();
        }

        if (SteamAPI.isSteamRunning(true)) {
            SteamAPI.shutdown();
        }
    }

    /**
     * Initializes Steam.
     * Can be called again with a different appId to switch games.
     * Due to the limitations of the {@link SteamAPI} only one game can be handled at the same time.
     *
     * @param appId of the game to use
     */
    @Override
    public boolean init(Integer appId) {
        log.debug("Initializing SteamAPI with appId: {}", appId);

        close();
        initSteamAppId(appId);
        initSteamNativeApi(appId);

        workshop = new SteamWorkshopService(new SteamWorkshopHandler(appId), mapper, new SteamUserHandler());
        this.appId = appId;

        return true;
    }

    /**
     * Will wait until all the open handlers got called by the {@link SteamAPI} callbacks.
     *
     * @param timeout optional for ending the loop after a certain duration
     */
    @Override
    public void awaits(@Nullable Duration timeout) {
        Instant end = null;
        if (timeout != null) {
            end = Instant.now().plus(timeout);
        }

        while (workshop.hasHandlers() && SteamAPI.isSteamRunning()) {
            runSteamAPICallbacks();

            // no timeout?
            if (timeout == null) {
                continue;
            }

            if (Instant.now().isAfter(end)) {
                log.debug("Interrupting runSteamAPI(). timeout of {} reached", timeout);
                return;
            }
        }
    }

    /**
     * Will run as long as the Steam Client is running.
     * Used for running {@link Steam} in a separate {@link Thread}
     */
    @Override
    public void run() {
        while (SteamAPI.isSteamRunning()) {
            runSteamAPICallbacks();
        }
    }

    /**
     * Executes the SteamAPI Callbacks.
     * Without it, the api will not anything =)
     */
    private void runSteamAPICallbacks() {
        SteamAPI.runCallbacks();

        try {
            Thread.sleep(1000 / 15);
        } catch (InterruptedException e) {
            log.debug("runSteamAPI interrupted", e);
        }
    }

    /**
     * The {@link SteamAPI} requires a file named like {@link SteamConfiguration#STEAM_APP_ID_TXT}
     * with the app id of the game you want to work with.
     *
     * @param appId to write into the file
     * @throws InitializeException when writing the file fails
     */
    private void initSteamAppId(Integer appId) {
        Path steamAppIdTxtPath = Paths.get(steamAppIdTxt);
        log.debug("Writing steam app id {} into {}", appId, steamAppIdTxtPath);

        try {
            fileService.write(steamAppIdTxtPath, appId.toString());
        } catch (Exception e) {
            throw new InitializeException("Error writing Steam App ID " + appId + " into " + steamAppIdTxtPath, e);
        }
    }

    /**
     * For initializing the {@link SteamAPI} library.
     * This requires a "steam_appid.txt" file with an existing and valid "appid" in it.
     *
     * @throws InitializeException when loading the native libraries or {@link SteamAPI} initialization fails
     */
    private void initSteamNativeApi(Integer appId) {
        log.debug("Initializing Steam native libraries and API");

        try {
            SteamAPI.loadLibraries();
            if (!SteamAPI.init() || SteamAPI.restartAppIfNecessary(appId)) {
                throw new InitializeException("""
                    SteamAPI initialization error. This could be because:
                    * Steam client is not running
                    * steam_appid.txt is not next to the executed app
                    * steam_appid.txt is empty
                    * steam_appid.txt contains a not existing or invalid app id
                    """);
            }
        } catch (SteamException e) {
            throw new InitializeException("Error extracting or loading native SteamAPI libraries", e);
        }
    }
}
