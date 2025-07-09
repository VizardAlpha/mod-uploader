package com.github.argon.moduploader.ui;

import io.quarkiverse.fx.FxApplication;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javafx.application.Application;

/**
 * Main entry point for the JavaFX application with Quarkus.
 * This class is marked with @QuarkusMain to indicate to Quarkus
 * that this is the main class to execute.
 */
@QuarkusMain
public class ExampleFxApplication implements QuarkusApplication {

    @Override
    public int run(final String... args) {
        // Launches the JavaFX application using the FxApplication class provided by the extension
        Application.launch(FxApplication.class, args);
        return 0;
    }
}