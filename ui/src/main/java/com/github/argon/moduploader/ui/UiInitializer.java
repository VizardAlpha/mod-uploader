package com.github.argon.moduploader.ui;

import io.quarkiverse.fx.FxPostStartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Initializes the user interface when the application starts.
 * This class observes the FxPostStartupEvent and loads the FXML file
 * to configure the main scene.
 */
@Slf4j
@ApplicationScoped
public class UiInitializer {

    @Inject
    FXMLLoader fxmlLoader;

    /**
     * Handles the post-startup event.
     * This method is called when the JavaFX application is ready and the main scene is available.
     *
     * @param event The post-startup event
     */
    public void onPostStartup(@Observes final FxPostStartupEvent event) {
        log.info("Initializing user interface...");
        try {
            Stage primaryStage = event.getPrimaryStage();
            primaryStage.setTitle("Quarkus FX Example");
            primaryStage.setWidth(600);
            primaryStage.setHeight(400);

            // Check that the FXML file exists
            var fxmlStream = getClass().getResourceAsStream("/fxml/MainView.fxml");
            if (fxmlStream == null) {
                log.error("FXML file not found at path /fxml/MainView.fxml");
                throw new RuntimeException("FXML file not found");
            }

            // Load the FXML file
            log.info("Loading FXML file...");
            Parent root = fxmlLoader.load(fxmlStream);

            // Create a scene and set it on the main stage
            Scene scene = new Scene(root);

            // Apply CSS to the scene
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/application.css")).toExternalForm());

            // Set the scene on the main stage
            primaryStage.setScene(scene);

            // Show the main stage
            primaryStage.show();

            log.info("User interface successfully initialized");
        } catch (Exception e) {
            log.error("Failed to initialize user interface", e);
        }
    }
}
