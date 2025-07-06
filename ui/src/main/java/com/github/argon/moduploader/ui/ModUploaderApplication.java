package com.github.argon.moduploader.ui;

import com.github.argon.moduploader.ui.config.ConfigService;
import com.github.argon.moduploader.ui.i18n.I18nService;
import com.github.argon.moduploader.ui.scene.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Main application class for the Mod Uploader UI.
 * This is the entry point for the JavaFX application.
 */
@Slf4j
public class ModUploaderApplication extends Application {

    private ConfigService configService;
    private I18nService i18nService;
    private SceneManager sceneManager;

    @Override
    public void init() {
        // Initialize services
        configService = new ConfigService();
        i18nService = new I18nService(configService);
        sceneManager = new SceneManager(configService, i18nService);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set the application title
            primaryStage.setTitle("Mod Uploader");

            // Set application icon
            try {
                // Try to load the icon
                var iconStream = getClass().getResourceAsStream("/icons/app-icon.png");
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                } else {
                    log.warn("App icon not found. Please add an icon file at /icons/app-icon.png");
                }
            } catch (Exception e) {
                log.warn("Failed to load app icon", e);
            }

            // Configure the primary stage
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Load the main scene
            sceneManager.loadMainScene(primaryStage);

            // Show the stage
            primaryStage.show();

            log.info("Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start application", e);
        }
    }

    @Override
    public void stop() {
        // Save configuration on application exit
        configService.saveConfig();
        log.info("Application stopped");
    }

    /**
     * Main method that launches the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            // If JavaFX runtime components are missing, try to launch with explicit module path
            if (e.getMessage() != null && e.getMessage().contains("JavaFX runtime components are missing")) {
                log.error("JavaFX runtime components are missing. Please make sure JavaFX is properly installed or use the packaged executable.", e);
                System.err.println("JavaFX runtime components are missing. Please make sure JavaFX is properly installed or use the packaged executable.");
                System.err.println("If running from IDE, add VM options: --module-path=\"path/to/javafx-sdk/lib\" --add-modules=javafx.controls,javafx.fxml");
            } else {
                log.error("Failed to launch application", e);
                e.printStackTrace();
            }
        }
    }
}
