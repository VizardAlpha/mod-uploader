package com.github.argon.moduploader.ui.controller;

import com.github.argon.moduploader.ui.service.ExampleService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jboss.logging.Logger;

/**
 * Main controller for the user interface.
 * This class is automatically detected by FXMLLoader and methods
 * annotated with @FXML are bound to the corresponding elements in the FXML file.
 */
@Dependent
public class MainController {

    private static final Logger LOG = Logger.getLogger(MainController.class);

    @FXML
    private TextField messageTextField;

    @FXML
    private Label resultLabel;

    @Inject
    ExampleService exampleService;

    /**
     * Initializes the controller. This method is called automatically
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        LOG.info("Initializing MainController");
        // Check if resultLabel is null before calling setText
        if (resultLabel != null) {
            resultLabel.setText("Enter a message and click the button");
        } else {
            LOG.warn("resultLabel is null during initialization");
        }
    }

    /**
     * Method called when the button is clicked.
     *
     * @param event The click event
     */
    @FXML
    public void onButtonClick(ActionEvent event) {
        String message = messageTextField.getText();
        if (message == null || message.trim().isEmpty()) {
            resultLabel.setText("Please enter a message");
            return;
        }

        String result = exampleService.processMessage(message);
        resultLabel.setText(result);
        LOG.info("Button clicked, result: " + result);
    }
}
