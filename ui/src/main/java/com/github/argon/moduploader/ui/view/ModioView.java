package com.github.argon.moduploader.ui.view;

import com.github.argon.moduploader.cli.command.modio.ModioLoginCommand;
import com.github.argon.moduploader.cli.command.modio.ModioUploadCommand;
import com.github.argon.moduploader.ui.config.ConfigService;
import com.github.argon.moduploader.ui.i18n.I18nService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * View for the Mod.io tab.
 */
@Slf4j
public class ModioView {

    private final ConfigService configService;
    private final I18nService i18nService;
    private final ExecutorService executorService;
    
    @Getter
    private final VBox root;
    
    private TextField apiKeyField;
    private TextField appIdField;
    private TextField publishedFileIdField;
    private TextField nameField;
    private TextArea descriptionArea;
    private TextArea changelogArea;
    private ComboBox<String> visibilityComboBox;
    private TextField contentFolderField;
    private TextField previewImageField;
    private TextField tagsField;
    private Button uploadButton;
    private Button listButton;
    private Button loginButton;
    private TextArea outputArea;
    
    /**
     * Creates a new ModioView.
     *
     * @param configService the configuration service
     * @param i18nService the internationalization service
     */
    public ModioView(ConfigService configService, I18nService i18nService) {
        this.configService = configService;
        this.i18nService = i18nService;
        this.executorService = Executors.newSingleThreadExecutor();
        
        // Create the root pane
        this.root = new VBox(10);
        this.root.setPadding(new Insets(20));
        
        // Initialize the UI
        initialize();
    }

    /**
     * Initializes the UI components.
     */
    private void initialize() {
        // Create a form grid
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        // API Key
        Label apiKeyLabel = new Label(i18nService.get("modio.apiKey"));
        apiKeyField = new TextField(configService.getConfig().getModioApiKey());
        formGrid.add(apiKeyLabel, 0, 0);
        formGrid.add(apiKeyField, 1, 0);
        
        // App ID
        Label appIdLabel = new Label(i18nService.get("modio.appId"));
        appIdField = new TextField(configService.getConfig().getSteamAppId().toString());
        formGrid.add(appIdLabel, 0, 1);
        formGrid.add(appIdField, 1, 1);
        
        // Published File ID
        Label publishedFileIdLabel = new Label(i18nService.get("modio.publishedFileId"));
        publishedFileIdField = new TextField();
        formGrid.add(publishedFileIdLabel, 0, 2);
        formGrid.add(publishedFileIdField, 1, 2);
        
        // Name
        Label nameLabel = new Label(i18nService.get("modio.name"));
        nameField = new TextField();
        formGrid.add(nameLabel, 0, 3);
        formGrid.add(nameField, 1, 3);
        
        // Description
        Label descriptionLabel = new Label(i18nService.get("modio.description"));
        descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        formGrid.add(descriptionLabel, 0, 4);
        formGrid.add(descriptionArea, 1, 4);
        
        // Changelog
        Label changelogLabel = new Label(i18nService.get("modio.changelog"));
        changelogArea = new TextArea();
        changelogArea.setPrefRowCount(3);
        formGrid.add(changelogLabel, 0, 5);
        formGrid.add(changelogArea, 1, 5);
        
        // Visibility
        Label visibilityLabel = new Label(i18nService.get("modio.visibility"));
        visibilityComboBox = new ComboBox<>(FXCollections.observableArrayList(
                i18nService.get("modio.visibility.public"),
                i18nService.get("modio.visibility.friends"),
                i18nService.get("modio.visibility.private")
        ));
        visibilityComboBox.setValue(i18nService.get("modio.visibility.public"));
        formGrid.add(visibilityLabel, 0, 6);
        formGrid.add(visibilityComboBox, 1, 6);
        
        // Content Folder
        Label contentFolderLabel = new Label(i18nService.get("modio.contentFolder"));
        contentFolderField = new TextField();
        Button contentFolderButton = new Button(i18nService.get("modio.browse"));
        contentFolderButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(i18nService.get("modio.contentFolder"));
            
            // Set initial directory
            String lastDirectory = configService.getConfig().getLastDirectory();
            if (lastDirectory != null) {
                directoryChooser.setInitialDirectory(new File(lastDirectory));
            }
            
            File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());
            if (selectedDirectory != null) {
                contentFolderField.setText(selectedDirectory.getAbsolutePath());
                configService.updateLastDirectory(selectedDirectory.getParent());
            }
        });
        HBox contentFolderBox = new HBox(10, contentFolderField, contentFolderButton);
        HBox.setHgrow(contentFolderField, Priority.ALWAYS);
        formGrid.add(contentFolderLabel, 0, 7);
        formGrid.add(contentFolderBox, 1, 7);
        
        // Preview Image
        Label previewImageLabel = new Label(i18nService.get("modio.previewImage"));
        previewImageField = new TextField();
        Button previewImageButton = new Button(i18nService.get("modio.browse"));
        previewImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(i18nService.get("modio.previewImage"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            
            // Set initial directory
            String lastDirectory = configService.getConfig().getLastDirectory();
            if (lastDirectory != null) {
                fileChooser.setInitialDirectory(new File(lastDirectory));
            }
            
            File selectedFile = fileChooser.showOpenDialog(root.getScene().getWindow());
            if (selectedFile != null) {
                previewImageField.setText(selectedFile.getAbsolutePath());
                configService.updateLastDirectory(selectedFile.getParent());
            }
        });
        HBox previewImageBox = new HBox(10, previewImageField, previewImageButton);
        HBox.setHgrow(previewImageField, Priority.ALWAYS);
        formGrid.add(previewImageLabel, 0, 8);
        formGrid.add(previewImageBox, 1, 8);
        
        // Tags
        Label tagsLabel = new Label(i18nService.get("modio.tags"));
        tagsField = new TextField();
        formGrid.add(tagsLabel, 0, 9);
        formGrid.add(tagsField, 1, 9);
        
        // Buttons
        uploadButton = new Button(i18nService.get("modio.upload"));
        uploadButton.getStyleClass().add("primary");
        uploadButton.setOnAction(event -> uploadMod());
        
        listButton = new Button(i18nService.get("modio.list"));
        listButton.setOnAction(event -> listMods());
        
        loginButton = new Button(i18nService.get("modio.login"));
        loginButton.setOnAction(event -> login());
        
        HBox buttonBox = new HBox(10, uploadButton, listButton, loginButton);
        
        // Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(10);
        
        // Add components to the root pane
        root.getChildren().addAll(
                formGrid,
                buttonBox,
                new Separator(),
                outputArea
        );
        
        // Save API key when it changes
        apiKeyField.textProperty().addListener((observable, oldValue, newValue) -> {
            configService.getConfig().setModioApiKey(newValue);
            configService.saveConfig();
        });
        
        log.info("ModioView initialized");
    }

    /**
     * Uploads a mod to Mod.io.
     */
    private void uploadMod() {
        try {
            // Disable the upload button
            uploadButton.setDisable(true);
            
            // Clear the output area
            outputArea.clear();
            
            // Build the command arguments
            List<String> args = new ArrayList<>();
            
            // Add the API key
            args.add("--api-key");
            args.add(apiKeyField.getText());
            
            // Add the app ID
            args.add("--app-id");
            args.add(appIdField.getText());
            
            // Add the published file ID if provided
            if (!publishedFileIdField.getText().isEmpty()) {
                args.add("--published-file-id");
                args.add(publishedFileIdField.getText());
            }
            
            // Add the name
            args.add("--name");
            args.add(nameField.getText());
            
            // Add the description if provided
            if (!descriptionArea.getText().isEmpty()) {
                args.add("--description");
                args.add(descriptionArea.getText());
            }
            
            // Add the changelog if provided
            if (!changelogArea.getText().isEmpty()) {
                args.add("--changelog");
                args.add(changelogArea.getText());
            }
            
            // Add the visibility if selected
            String visibility = visibilityComboBox.getValue();
            if (i18nService.get("modio.visibility.public").equals(visibility)) {
                args.add("--visibility");
                args.add("Public");
            } else if (i18nService.get("modio.visibility.friends").equals(visibility)) {
                args.add("--visibility");
                args.add("FriendsOnly");
            } else if (i18nService.get("modio.visibility.private").equals(visibility)) {
                args.add("--visibility");
                args.add("Private");
            }
            
            // Add the content folder
            args.add("--content-folder");
            args.add(contentFolderField.getText());
            
            // Add the preview image
            args.add("--image");
            args.add(previewImageField.getText());
            
            // Add tags if provided
            if (!tagsField.getText().isEmpty()) {
                String[] tags = tagsField.getText().split(",");
                for (String tag : tags) {
                    args.add("--tags");
                    args.add(tag.trim());
                }
            }
            
            // Execute the command in a background thread
            executorService.submit(() -> {
                try {
                    // Redirect System.out to the output area
                    System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                        private final StringBuilder buffer = new StringBuilder();
                        
                        @Override
                        public void write(int b) {
                            char c = (char) b;
                            buffer.append(c);
                            if (c == '\n') {
                                final String line = buffer.toString();
                                buffer.setLength(0);
                                
                                // Update the UI on the JavaFX thread
                                javafx.application.Platform.runLater(() -> {
                                    outputArea.appendText(line);
                                });
                            }
                        }
                    }));
                    
                    // Create and execute the command
                    ModioUploadCommand command = new ModioUploadCommand();
                    new CommandLine(command).execute(args.toArray(new String[0]));
                    
                    // Enable the upload button on the JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        uploadButton.setDisable(false);
                    });
                } catch (Exception e) {
                    // Log the error
                    log.error("Failed to upload mod", e);
                    
                    // Show the error in the output area
                    javafx.application.Platform.runLater(() -> {
                        outputArea.appendText("Error: " + e.getMessage() + "\n");
                        uploadButton.setDisable(false);
                    });
                }
            });
        } catch (Exception e) {
            // Log the error
            log.error("Failed to upload mod", e);
            
            // Show the error in the output area
            outputArea.appendText("Error: " + e.getMessage() + "\n");
            
            // Enable the upload button
            uploadButton.setDisable(false);
        }
    }

    /**
     * Lists mods in Mod.io.
     */
    private void listMods() {
        // TODO: Implement listing mods
        outputArea.appendText("Listing mods is not implemented yet.\n");
    }

    /**
     * Logs in to Mod.io.
     */
    private void login() {
        try {
            // Disable the login button
            loginButton.setDisable(true);
            
            // Clear the output area
            outputArea.clear();
            
            // Build the command arguments
            List<String> args = new ArrayList<>();
            
            // Add the API key
            args.add("--api-key");
            args.add(apiKeyField.getText());
            
            // Execute the command in a background thread
            executorService.submit(() -> {
                try {
                    // Redirect System.out to the output area
                    System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                        private final StringBuilder buffer = new StringBuilder();
                        
                        @Override
                        public void write(int b) {
                            char c = (char) b;
                            buffer.append(c);
                            if (c == '\n') {
                                final String line = buffer.toString();
                                buffer.setLength(0);
                                
                                // Update the UI on the JavaFX thread
                                javafx.application.Platform.runLater(() -> {
                                    outputArea.appendText(line);
                                });
                            }
                        }
                    }));
                    
                    // Create and execute the command
                    ModioLoginCommand command = new ModioLoginCommand();
                    new CommandLine(command).execute(args.toArray(new String[0]));
                    
                    // Enable the login button on the JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        loginButton.setDisable(false);
                    });
                } catch (Exception e) {
                    // Log the error
                    log.error("Failed to login", e);
                    
                    // Show the error in the output area
                    javafx.application.Platform.runLater(() -> {
                        outputArea.appendText("Error: " + e.getMessage() + "\n");
                        loginButton.setDisable(false);
                    });
                }
            });
        } catch (Exception e) {
            // Log the error
            log.error("Failed to login", e);
            
            // Show the error in the output area
            outputArea.appendText("Error: " + e.getMessage() + "\n");
            
            // Enable the login button
            loginButton.setDisable(false);
        }
    }
}