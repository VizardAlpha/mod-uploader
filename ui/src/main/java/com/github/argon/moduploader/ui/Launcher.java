package com.github.argon.moduploader.ui;

import lombok.extern.slf4j.Slf4j;

/**
 * Launcher class for the Mod Uploader application.
 * This class is responsible for launching the JavaFX application with the correct module path.
 */
@Slf4j
public class Launcher {

    /**
     * Main method that launches the application.
     * This method sets up the JavaFX environment and launches the ModUploaderApplication.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            // Launch the JavaFX application
            ModUploaderApplication.main(args);
        } catch (Exception e) {
            log.error("Failed to launch application", e);
            System.err.println("Failed to launch application: " + e.getMessage());
            
            // If JavaFX runtime components are missing, provide helpful error message
            if (e.getMessage() != null && e.getMessage().contains("JavaFX runtime components are missing")) {
                System.err.println("\nJavaFX runtime components are missing. Please make sure JavaFX is properly installed or use the packaged executable.");
                System.err.println("If running from IDE, add VM options: --module-path=\"path/to/javafx-sdk/lib\" --add-modules=javafx.controls,javafx.fxml,javafx.graphics");
                System.err.println("\nFor Windows users:");
                System.err.println("1. Download JavaFX SDK from https://gluonhq.com/products/javafx/");
                System.err.println("2. Extract it to a location on your computer");
                System.err.println("3. Add VM options to your run configuration:");
                System.err.println("   --module-path=\"C:\\path\\to\\javafx-sdk\\lib\" --add-modules=javafx.controls,javafx.fxml,javafx.graphics");
            }
            
            e.printStackTrace();
        }
    }
}