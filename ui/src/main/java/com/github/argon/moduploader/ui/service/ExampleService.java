package com.github.argon.moduploader.ui.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Example service that will be injected into the controller.
 */
@ApplicationScoped
public class ExampleService {

    private static final Logger LOG = Logger.getLogger(ExampleService.class);

    /**
     * Example method that will be called from the controller.
     *
     * @param message The message to process
     * @return The processed message
     */
    public String processMessage(String message) {
        LOG.info("Processing message: " + message);
        return "Processed message: " + message.toUpperCase();
    }
}
