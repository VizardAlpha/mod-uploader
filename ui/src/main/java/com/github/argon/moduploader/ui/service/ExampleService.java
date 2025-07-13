package com.github.argon.moduploader.ui.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

/**
 * Example service that will be injected into the controller.
 */
@Slf4j
@ApplicationScoped
public class ExampleService {

    /**
     * Example method that will be called from the controller.
     *
     * @param message The message to process
     * @return The processed message
     */
    public String processMessage(String message) {
        log.info("Processing message: " + message);
        return "Processed message: " + message.toUpperCase();
    }
}
