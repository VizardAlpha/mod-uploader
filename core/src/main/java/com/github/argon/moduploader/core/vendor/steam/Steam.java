package com.github.argon.moduploader.core.vendor.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.github.argon.moduploader.core.Blockable;
import com.github.argon.moduploader.core.Initializable;
import com.github.argon.moduploader.core.InitializeException;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreClient;
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
public class Steam implements Closeable, Blockable, Runnable, Initializable<Integer> {
    public static final String STEAM_APP_ID_TXT = "steam_appid.txt";

    @Getter
    private final String steamAppIdTxt;
    @Getter
    private SteamWorkshopService workshop;

    private final IFileService fileService;
    private final SteamStoreClient storeClient;
    private final SteamMapper mapper;

    public Steam(Integer appId, IFileService fileService, SteamStoreClient storeClient, SteamMapper mapper) throws InitializeException {
        this(appId, fileService, STEAM_APP_ID_TXT, storeClient, mapper);
    }

    /**
     * @param appId of the Steam game / application
     * @param fileService for writing the steamAppIdTxt
     * @param steamAppIdTxt name of the steam appid txt file, which needs to be present next to this app
     * @throws InitializeException when {@link SteamAPI} initialization or steamAppIdTxt file creation fails
     */
    public Steam(Integer appId, IFileService fileService, String steamAppIdTxt, SteamStoreClient storeClient, SteamMapper mapper) throws InitializeException {
        this.steamAppIdTxt = steamAppIdTxt;
        this.fileService = fileService;
        this.mapper = mapper;
        this.storeClient = storeClient;

        init(appId);
    }

    /**
     * Will throw away all open steam handlers
     */
    @Override
    public void close() {
        workshop.close();
        SteamAPI.shutdown();
    }

    /**
     * Initializes Steam
     *
     * @param appId of the game to use
     */
    @Override
    public boolean init(Integer appId) throws InitializeException {
        log.debug("Initializing SteamAPI with appId: {}", appId);
        initSteamAppId(appId);
        initSteamNativeApi();

        // make sure all open handlers are thrown away
        if (workshop != null) {
            workshop.close();
        }

        workshop = new SteamWorkshopService(appId, mapper);

        return true;
    }

    /**
     * Will wait until all the open handlers got called by the {@link SteamAPI} callbacks.
     *
     * @param timeout optional for ending the loop after a certain duration
     */
    @Override
    public void block(@Nullable Duration timeout) {
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
     * The {@link SteamAPI} requires a file named like {@link Steam#STEAM_APP_ID_TXT}
     * with the app id of the game you want to work with.
     *
     * @param appId to write into the file
     * @throws InitializeException when writing the file fails
     */
    private void initSteamAppId(Integer appId) throws InitializeException {
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
    private void initSteamNativeApi() throws InitializeException {
        log.debug("Initializing Steam native libraries and API");
        try {
            SteamAPI.loadLibraries();
            if (!SteamAPI.init()) {
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
