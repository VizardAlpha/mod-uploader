package com.github.argon.moduploader.vendor.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.github.argon.moduploader.vendor.VendorException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * For interacting with Steam.
 * Contains all logic to initialize the native {@link SteamAPI}.
 */
public class Steam implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(Steam.class);
    public static final String STEAM_APP_ID_TXT = "steam_appid.txt";

    @Getter
    private Integer appId;
    @Getter
    private final Path currentWorkingDir;
    private final String steamAppIdTxt;
    @Getter
    private final SteamWorkshop workshop;

    public Steam(Integer appId, Path currentWorkingDir) throws VendorException {
        this(appId, currentWorkingDir, STEAM_APP_ID_TXT);
    }

    /**
     * @param appId of the Steam game / application
     * @param currentWorkingDir directory where this application thinks it runs from
     * @param steamAppIdTxt name of the steam appid txt file, which needs to be present next to this app
     * @throws VendorException when {@link SteamAPI} initialization or steamAppIdTxt file creation fails
     */
    public Steam(Integer appId, Path currentWorkingDir, String steamAppIdTxt) throws VendorException {
        this.currentWorkingDir = currentWorkingDir;
        this.steamAppIdTxt = steamAppIdTxt;

        initSteamAppId(appId);
        initSteamNativeApi();

        this.workshop = new SteamWorkshop(appId);
    }

    private void initSteamAppId(Integer appId) throws VendorException {
        Path steamAppIdTxtPath = currentWorkingDir.resolve(steamAppIdTxt);

        try {
            if (!Files.exists(steamAppIdTxtPath)) {
                Files.createFile(steamAppIdTxtPath);
            }

            Files.writeString(steamAppIdTxtPath, appId.toString());
            this.appId = appId;
            log.debug("Steam AppId {} saved to {}", appId, steamAppIdTxtPath);
        } catch (Exception e) {
            throw new VendorException("Error writing Steam App ID " + appId + " into " + steamAppIdTxtPath, e);
        }
    }

    @Override
    public void close() {
        workshop.close();
        SteamAPI.shutdown();
    }

    private void initSteamNativeApi() throws VendorException {
        try {
            SteamAPI.loadLibraries();
            if (!SteamAPI.init()) {
                throw new VendorException("Steamworks initialization error, e.g. Steam client not running");
            }
        } catch (SteamException e) {
            throw new VendorException("Error extracting or loading native Steam libraries", e);
        }
    }
}
