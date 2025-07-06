package com.github.argon.moduploader.ui.config;

import lombok.Data;

/**
 * Class representing user configuration settings.
 * This is serialized to/from JSON for persistence.
 */
@Data
public class UserConfig {

    /**
     * The language setting (e.g., "en", "fr").
     * Default is English.
     */
    private String language = "en";

    /**
     * The theme setting (e.g., "light", "dark").
     * Default is the Steam-themed dark mode.
     */
    private String theme = "steam-dark";

    /**
     * The last directory used for file operations.
     * Default is the user's home directory.
     */
    private String lastDirectory = System.getProperty("user.home");
    
    /**
     * Steam app ID for the game.
     * Default is 480 (Spacewar).
     */
    private Integer steamAppId = 480;
    
    /**
     * Mod.io API key.
     */
    private String modioApiKey;
}