package com.github.argon.moduploader.ui.i18n;

import com.github.argon.moduploader.ui.config.ConfigService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Service for handling internationalization (i18n) in the application.
 * Manages loading and accessing localized strings from properties files.
 */
@Slf4j
public class I18nService {

    private static final String BUNDLE_BASE_NAME = "i18n/messages";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final Map<String, Locale> SUPPORTED_LOCALES = new HashMap<>();
    
    static {
        // Add supported locales
        SUPPORTED_LOCALES.put("en", Locale.ENGLISH);
        SUPPORTED_LOCALES.put("fr", Locale.FRENCH);
    }
    
    private final ConfigService configService;
    
    @Getter
    private ResourceBundle resourceBundle;
    
    @Getter
    private StringProperty currentLanguage = new SimpleStringProperty();

    /**
     * Creates a new I18nService with the given ConfigService.
     *
     * @param configService the configuration service
     */
    public I18nService(ConfigService configService) {
        this.configService = configService;
        
        // Load the resource bundle for the configured language
        String language = configService.getConfig().getLanguage();
        loadResourceBundle(language);
        
        // Set the current language property
        currentLanguage.set(language);
    }

    /**
     * Gets a localized string for the given key.
     *
     * @param key the key for the localized string
     * @return the localized string, or the key if not found
     */
    public String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            log.warn("Missing translation for key: {}", key);
            return key;
        }
    }

    /**
     * Changes the application language.
     *
     * @param language the language code (e.g., "en", "fr")
     * @return true if the language was changed, false otherwise
     */
    public boolean changeLanguage(String language) {
        if (!SUPPORTED_LOCALES.containsKey(language)) {
            log.warn("Unsupported language: {}", language);
            return false;
        }
        
        // Load the resource bundle for the new language
        if (loadResourceBundle(language)) {
            // Update the configuration
            configService.updateLanguage(language);
            
            // Update the current language property
            currentLanguage.set(language);
            
            return true;
        }
        
        return false;
    }

    /**
     * Loads the resource bundle for the given language.
     *
     * @param language the language code
     * @return true if the resource bundle was loaded, false otherwise
     */
    private boolean loadResourceBundle(String language) {
        try {
            Locale locale = SUPPORTED_LOCALES.getOrDefault(language, SUPPORTED_LOCALES.get(DEFAULT_LANGUAGE));
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            return true;
        } catch (Exception e) {
            log.error("Failed to load resource bundle for language: {}", language, e);
            
            // Try to load the default language
            if (!language.equals(DEFAULT_LANGUAGE)) {
                log.info("Falling back to default language: {}", DEFAULT_LANGUAGE);
                return loadResourceBundle(DEFAULT_LANGUAGE);
            }
            
            return false;
        }
    }

    /**
     * Gets a map of supported languages.
     *
     * @return a map of language codes to language names
     */
    public Map<String, String> getSupportedLanguages() {
        Map<String, String> languages = new HashMap<>();
        languages.put("en", "English");
        languages.put("fr", "Français");
        return languages;
    }
}