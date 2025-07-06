package com.github.argon.moduploader.ui.scene;

import com.github.argon.moduploader.ui.config.ConfigService;
import com.github.argon.moduploader.ui.i18n.I18nService;
import com.github.argon.moduploader.ui.view.MainView;
import com.github.argon.moduploader.ui.view.SettingsView;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Manager for handling scenes and navigation in the application.
 */
@Slf4j
@RequiredArgsConstructor
public class SceneManager {

    private final ConfigService configService;
    private final I18nService i18nService;

    private Scene mainScene;
    private MainView mainView;

    /**
     * Loads the main scene and sets it on the primary stage.
     *
     * @param primaryStage the primary stage
     */
    public void loadMainScene(Stage primaryStage) {
        try {
            // Create the main view
            mainView = new MainView(configService, i18nService, this);

            // Create the scene
            mainScene = new Scene(mainView.getRoot(), 1024, 768);

            // Apply the theme
            applyTheme(mainScene);

            // Set the scene on the stage
            primaryStage.setScene(mainScene);

            log.info("Main scene loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load main scene", e);
            throw new RuntimeException("Failed to load main scene", e);
        }
    }

    /**
     * Shows the settings dialog.
     *
     * @param owner the owner window
     */
    public void showSettingsDialog(Stage owner) {
        try {
            // Create a new stage for the settings dialog
            Stage settingsStage = new Stage();
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.initOwner(owner);
            settingsStage.setTitle(i18nService.get("settings.title"));

            // Set the icon
            try {
                // Try to load the icon
                var iconStream = getClass().getResourceAsStream("/icons/app-icon.png");
                if (iconStream != null) {
                    settingsStage.getIcons().add(new Image(iconStream));
                } else {
                    log.warn("App icon not found. Please add an icon file at /icons/app-icon.png");
                }
            } catch (Exception e) {
                log.warn("Failed to load app icon", e);
            }

            // Create the settings view
            SettingsView settingsView = new SettingsView(configService, i18nService, settingsStage);

            // Create the scene
            Scene settingsScene = new Scene(settingsView.getRoot(), 400, 300);

            // Apply the theme
            applyTheme(settingsScene);

            // Set the scene on the stage
            settingsStage.setScene(settingsScene);

            // Show the dialog
            settingsStage.showAndWait();

            // Apply the theme to the main scene if it was changed
            applyTheme(mainScene);

            log.info("Settings dialog shown");
        } catch (Exception e) {
            log.error("Failed to show settings dialog", e);
        }
    }

    /**
     * Applies the configured theme to the given scene.
     *
     * @param scene the scene to apply the theme to
     */
    private void applyTheme(Scene scene) {
        String theme = configService.getConfig().getTheme();
        String cssPath = "/styles/" + theme + ".css";

        // Clear existing stylesheets
        scene.getStylesheets().clear();

        try {
            // Try to load the stylesheet
            var cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                log.info("Applied theme: {}", theme);
            } else {
                log.warn("Theme CSS not found: {}. Using default JavaFX styling.", cssPath);
            }

            // Force a refresh of the scene to ensure the theme is applied
            scene.getRoot().applyCss();
        } catch (Exception e) {
            log.error("Failed to apply theme: {}", theme, e);
        }
    }
}
