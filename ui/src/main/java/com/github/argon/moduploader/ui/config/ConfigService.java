package com.github.argon.moduploader.ui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service for managing application configuration.
 * Handles loading and saving configuration to/from a JSON file.
 */
@Slf4j
public class ConfigService {

    private static final String CONFIG_DIRECTORY = System.getProperty("user.home") + File.separator + ".moduploader";
    private static final String CONFIG_FILE = "config.json";
    private final Path configPath;
    private final ObjectMapper objectMapper;
    
    @Getter
    private UserConfig config;

    /**
     * Creates a new ConfigService and loads the configuration.
     */
    public ConfigService() {
        this.objectMapper = new ObjectMapper();
        this.configPath = Paths.get(CONFIG_DIRECTORY, CONFIG_FILE);
        this.config = loadConfig();
    }

    /**
     * Loads the configuration from the config file.
     * If the file doesn't exist or can't be read, returns a default configuration.
     *
     * @return the loaded configuration or a default configuration
     */
    private UserConfig loadConfig() {
        try {
            // Create config directory if it doesn't exist
            Files.createDirectories(Paths.get(CONFIG_DIRECTORY));
            
            // If config file exists, read it
            if (Files.exists(configPath)) {
                return objectMapper.readValue(configPath.toFile(), UserConfig.class);
            }
        } catch (IOException e) {
            log.error("Failed to load configuration", e);
        }
        
        // Return default configuration if loading fails
        return new UserConfig();
    }

    /**
     * Saves the current configuration to the config file.
     */
    public void saveConfig() {
        try {
            // Create config directory if it doesn't exist
            Files.createDirectories(Paths.get(CONFIG_DIRECTORY));
            
            // Write config to file
            objectMapper.writeValue(configPath.toFile(), config);
            log.info("Configuration saved successfully");
        } catch (IOException e) {
            log.error("Failed to save configuration", e);
        }
    }

    /**
     * Updates the language setting and saves the configuration.
     *
     * @param language the new language
     */
    public void updateLanguage(String language) {
        config.setLanguage(language);
        saveConfig();
    }

    /**
     * Updates the theme setting and saves the configuration.
     *
     * @param theme the new theme
     */
    public void updateTheme(String theme) {
        config.setTheme(theme);
        saveConfig();
    }
    
    /**
     * Updates the last used directory and saves the configuration.
     *
     * @param lastDirectory the last used directory
     */
    public void updateLastDirectory(String lastDirectory) {
        config.setLastDirectory(lastDirectory);
        saveConfig();
    }
}