package com.github.argon.moduploader.ui.view;

import com.codedisaster.steamworks.SteamRemoteStorage;
import com.github.argon.moduploader.cli.command.steam.SteamUploadCommand;
import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.vendor.steam.model.SteamMod;
import com.github.argon.moduploader.ui.config.ConfigService;
import com.github.argon.moduploader.ui.i18n.I18nService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * View for the Steam Workshop tab.
 */
@Slf4j
public class SteamWorkshopView {

    private final ConfigService configService;
    private final I18nService i18nService;
    private final ExecutorService executorService;
    private final FileService fileService;
    private final SettingsView settingsView;

    @Getter
    private final VBox root;

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
    private Button editButton;
    private Button deleteRemoteButton;
    private Button deleteLocalButton;

    private TableView<RemoteModTableItem> remoteModsTable;
    private TableView<LocalModTableItem> localModsTable;
    private ObservableList<RemoteModTableItem> remoteModsData = FXCollections.observableArrayList();
    private ObservableList<LocalModTableItem> localModsData = FXCollections.observableArrayList();

    // Table item classes for binding to TableView
    public static class RemoteModTableItem {
        private final Long publishedFileId;
        private final String title;
        private final String description;
        private final String tags;
        private final Integer fileSize;
        private final String timeCreated;
        private final String timeUpdated;
        private final Integer votesUp;
        private final Integer votesDown;

        public RemoteModTableItem(SteamMod.Remote mod) {
            this.publishedFileId = mod.publishedFileId();
            this.title = mod.title();
            this.description = mod.description();
            this.tags = String.join(", ", mod.tags());
            this.fileSize = mod.fileSize();
            this.timeCreated = formatInstant(mod.timeCreated());
            this.timeUpdated = formatInstant(mod.timeUpdated());
            this.votesUp = mod.votesUp();
            this.votesDown = mod.votesDown();
        }

        private String formatInstant(Instant instant) {
            return instant != null ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(instant) : "";
        }

        public Long getPublishedFileId() { return publishedFileId; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getTags() { return tags; }
        public Integer getFileSize() { return fileSize; }
        public String getTimeCreated() { return timeCreated; }
        public String getTimeUpdated() { return timeUpdated; }
        public Integer getVotesUp() { return votesUp; }
        public Integer getVotesDown() { return votesDown; }
    }

    public static class LocalModTableItem {
        private final Long publishedFileId;
        private final String title;
        private final String description;
        private final String tags;
        private final String contentFolder;
        private final String previewImage;

        public LocalModTableItem(SteamMod.Local mod) {
            this.publishedFileId = mod.publishedFileId();
            this.title = mod.title();
            this.description = mod.description();
            this.tags = String.join(", ", mod.tags());
            this.contentFolder = mod.contentFolder().toString();
            this.previewImage = mod.previewImage().toString();
        }

        public Long getPublishedFileId() { return publishedFileId; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getTags() { return tags; }
        public String getContentFolder() { return contentFolder; }
        public String getPreviewImage() { return previewImage; }
    }

    /**
     * Creates a new SteamWorkshopView.
     *
     * @param configService the configuration service
     * @param i18nService the internationalization service
     * @param settingsView the settings view for logging
     */
    public SteamWorkshopView(ConfigService configService, I18nService i18nService, SettingsView settingsView) {
        this.configService = configService;
        this.i18nService = i18nService;
        this.settingsView = settingsView;
        this.executorService = Executors.newSingleThreadExecutor();
        this.fileService = new FileService();

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
        // Create a container for the form grids (side by side)
        HBox formContainer = new HBox(20);

        // Create left form grid
        GridPane leftFormGrid = new GridPane();
        leftFormGrid.setHgap(10);
        leftFormGrid.setVgap(10);
        HBox.setHgrow(leftFormGrid, Priority.ALWAYS);

        // Create right form grid
        GridPane rightFormGrid = new GridPane();
        rightFormGrid.setHgap(10);
        rightFormGrid.setVgap(10);
        HBox.setHgrow(rightFormGrid, Priority.ALWAYS);

        // App ID - Auto-fill from steam_appid.txt if it exists
        Label appIdLabel = new Label(i18nService.get("steam.appId"));
        String appId = configService.getConfig().getSteamAppId().toString();

        // Try to read from steam_appid.txt
        try {
            Path steamAppIdPath = Paths.get("steam_appid.txt");
            if (Files.exists(steamAppIdPath)) {
                String steamAppId = Files.readString(steamAppIdPath).trim();
                if (!steamAppId.isEmpty()) {
                    appId = steamAppId;
                }
            }
        } catch (Exception e) {
            log.error("Failed to read steam_appid.txt", e);
        }

        appIdField = new TextField(appId);
        leftFormGrid.add(appIdLabel, 0, 0);
        leftFormGrid.add(appIdField, 1, 0);

        // Published File ID
        Label publishedFileIdLabel = new Label(i18nService.get("steam.publishedFileId"));
        publishedFileIdField = new TextField();
        leftFormGrid.add(publishedFileIdLabel, 0, 1);
        leftFormGrid.add(publishedFileIdField, 1, 1);

        // Name
        Label nameLabel = new Label(i18nService.get("steam.name"));
        nameField = new TextField();
        leftFormGrid.add(nameLabel, 0, 2);
        leftFormGrid.add(nameField, 1, 2);

        // Description
        Label descriptionLabel = new Label(i18nService.get("steam.description"));
        descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        leftFormGrid.add(descriptionLabel, 0, 3);
        leftFormGrid.add(descriptionArea, 1, 3);

        // Changelog
        Label changelogLabel = new Label(i18nService.get("steam.changelog"));
        changelogArea = new TextArea();
        changelogArea.setPrefRowCount(3);
        leftFormGrid.add(changelogLabel, 0, 4);
        leftFormGrid.add(changelogArea, 1, 4);

        // Visibility
        Label visibilityLabel = new Label(i18nService.get("steam.visibility"));
        visibilityComboBox = new ComboBox<>(FXCollections.observableArrayList(
                i18nService.get("steam.visibility.public"),
                i18nService.get("steam.visibility.friends"),
                i18nService.get("steam.visibility.private")
        ));
        visibilityComboBox.setValue(i18nService.get("steam.visibility.public"));
        rightFormGrid.add(visibilityLabel, 0, 0);
        rightFormGrid.add(visibilityComboBox, 1, 0);

        // Content Folder
        Label contentFolderLabel = new Label(i18nService.get("steam.contentFolder"));
        contentFolderField = new TextField();
        Button contentFolderButton = new Button(i18nService.get("steam.browse"));
        contentFolderButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(i18nService.get("steam.contentFolder"));

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
        rightFormGrid.add(contentFolderLabel, 0, 1);
        rightFormGrid.add(contentFolderBox, 1, 1);

        // Preview Image
        Label previewImageLabel = new Label(i18nService.get("steam.previewImage"));
        previewImageField = new TextField();
        Button previewImageButton = new Button(i18nService.get("steam.browse"));
        previewImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(i18nService.get("steam.previewImage"));
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
        rightFormGrid.add(previewImageLabel, 0, 2);
        rightFormGrid.add(previewImageBox, 1, 2);

        // Tags
        Label tagsLabel = new Label(i18nService.get("steam.tags"));
        tagsField = new TextField();
        rightFormGrid.add(tagsLabel, 0, 3);
        rightFormGrid.add(tagsField, 1, 3);

        // Add both grids to the container
        formContainer.getChildren().addAll(leftFormGrid, rightFormGrid);

        // Buttons
        uploadButton = new Button(i18nService.get("steam.upload"));
        uploadButton.getStyleClass().add("primary");
        uploadButton.setOnAction(event -> uploadMod());

        listButton = new Button(i18nService.get("steam.list"));
        listButton.setOnAction(event -> listMods());

        editButton = new Button(i18nService.get("steam.edit"));
        editButton.setOnAction(event -> editSelectedMod());
        editButton.setDisable(true);

        deleteRemoteButton = new Button(i18nService.get("steam.deleteRemote"));
        deleteRemoteButton.setOnAction(event -> deleteRemoteMod());
        deleteRemoteButton.setDisable(true);

        deleteLocalButton = new Button(i18nService.get("steam.deleteLocal"));
        deleteLocalButton.setOnAction(event -> deleteLocalMod());
        deleteLocalButton.setDisable(true);

        HBox buttonBox = new HBox(10, uploadButton, listButton, editButton, deleteRemoteButton, deleteLocalButton);

        // Remote Mods Table
        Label remoteModsLabel = new Label(i18nService.get("steam.remoteMods"));
        remoteModsLabel.getStyleClass().add("header");

        remoteModsTable = new TableView<>();
        remoteModsTable.setItems(remoteModsData);

        TableColumn<RemoteModTableItem, Long> remoteIdColumn = new TableColumn<>(i18nService.get("steam.id"));
        remoteIdColumn.setCellValueFactory(new PropertyValueFactory<>("publishedFileId"));

        TableColumn<RemoteModTableItem, String> remoteTitleColumn = new TableColumn<>(i18nService.get("steam.title"));
        remoteTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        remoteTitleColumn.setPrefWidth(200);

        TableColumn<RemoteModTableItem, String> remoteTagsColumn = new TableColumn<>(i18nService.get("steam.tags"));
        remoteTagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        remoteTagsColumn.setPrefWidth(150);

        TableColumn<RemoteModTableItem, Integer> remoteSizeColumn = new TableColumn<>(i18nService.get("steam.size"));
        remoteSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));

        TableColumn<RemoteModTableItem, String> remoteUpdatedColumn = new TableColumn<>(i18nService.get("steam.updated"));
        remoteUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("timeUpdated"));

        // Make table columns expand with the app
        remoteModsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        remoteModsTable.getColumns().addAll(
            remoteIdColumn, remoteTitleColumn, remoteTagsColumn, remoteSizeColumn, remoteUpdatedColumn
        );

        remoteModsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteRemoteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteRemoteButton.setDisable(true);
            }
        });

        // Local Mods Table
        Label localModsLabel = new Label(i18nService.get("steam.localMods"));
        localModsLabel.getStyleClass().add("header");

        localModsTable = new TableView<>();
        localModsTable.setItems(localModsData);

        TableColumn<LocalModTableItem, Long> localIdColumn = new TableColumn<>(i18nService.get("steam.id"));
        localIdColumn.setCellValueFactory(new PropertyValueFactory<>("publishedFileId"));

        TableColumn<LocalModTableItem, String> localTitleColumn = new TableColumn<>(i18nService.get("steam.title"));
        localTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        localTitleColumn.setPrefWidth(200);

        TableColumn<LocalModTableItem, String> localTagsColumn = new TableColumn<>(i18nService.get("steam.tags"));
        localTagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        localTagsColumn.setPrefWidth(150);

        TableColumn<LocalModTableItem, String> localFolderColumn = new TableColumn<>(i18nService.get("steam.folder"));
        localFolderColumn.setCellValueFactory(new PropertyValueFactory<>("contentFolder"));
        localFolderColumn.setPrefWidth(200);

        // Make table columns expand with the app
        localModsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        localModsTable.getColumns().addAll(
            localIdColumn, localTitleColumn, localTagsColumn, localFolderColumn
        );

        localModsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteLocalButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteLocalButton.setDisable(true);
            }
        });

        // Add components to the root pane
        root.getChildren().addAll(
                formContainer,
                buttonBox,
                new Separator(),
                remoteModsLabel,
                remoteModsTable,
                new Separator(),
                localModsLabel,
                localModsTable
        );

        log.info("SteamWorkshopView initialized");
    }

    /**
     * Uploads a mod to the Steam Workshop.
     */
    private void uploadMod() {
        try {
            // Disable the upload button
            uploadButton.setDisable(true);

            // Clear the output area
            settingsView.appendToLog(""); // Clear by sending empty string

            // Build the command arguments
            List<String> args = new ArrayList<>();

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
            if (i18nService.get("steam.visibility.public").equals(visibility)) {
                args.add("--visibility");
                args.add("Public");
            } else if (i18nService.get("steam.visibility.friends").equals(visibility)) {
                args.add("--visibility");
                args.add("FriendsOnly");
            } else if (i18nService.get("steam.visibility.private").equals(visibility)) {
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
                                    settingsView.appendToLog(line);
                                });
                            }
                        }
                    }));

                    // Create and execute the command
                    SteamUploadCommand command = new SteamUploadCommand();
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
                        settingsView.appendToLog("Error: " + e.getMessage());
                        uploadButton.setDisable(false);
                    });
                }
            });
        } catch (Exception e) {
            // Log the error
            log.error("Failed to upload mod", e);

            // Show the error in the output area
            settingsView.appendToLog("Error: " + e.getMessage());

            // Enable the upload button
            uploadButton.setDisable(false);
        }
    }

    /**
     * Lists mods in the Steam Workshop.
     */
    private void listMods() {
        try {
            // Disable the list button
            listButton.setDisable(true);

            // Clear the output area
            settingsView.appendToLog(""); // Clear by sending empty string
            settingsView.appendToLog("Fetching your published mods...");

            // Clear the remote mods table
            remoteModsData.clear();

            // Build the command arguments
            List<String> args = new ArrayList<>();

            // Add the app ID
            args.add("--app-id");
            args.add(appIdField.getText());

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
                                    settingsView.appendToLog(line);
                                });
                            }
                        }
                    }));

                    // Create a custom command that will populate our table
                    com.github.argon.moduploader.cli.command.steam.SteamListCommand command = 
                        new com.github.argon.moduploader.cli.command.steam.SteamListCommand();

                    // Execute the command
                    new CommandLine(command).execute(args.toArray(new String[0]));

                    // Enable the list button on the JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        listButton.setDisable(false);
                    });
                } catch (Exception e) {
                    // Log the error
                    log.error("Failed to list mods", e);

                    // Show the error in the output area
                    javafx.application.Platform.runLater(() -> {
                        settingsView.appendToLog("Error: " + e.getMessage());
                        listButton.setDisable(false);
                    });
                }
            });
        } catch (Exception e) {
            // Log the error
            log.error("Failed to list mods", e);

            // Show the error in the output area
            settingsView.appendToLog("Error: " + e.getMessage());

            // Enable the list button
            listButton.setDisable(false);
        }
    }

    /**
     * Edits the selected mod by populating the form fields.
     */
    private void editSelectedMod() {
        RemoteModTableItem remoteMod = remoteModsTable.getSelectionModel().getSelectedItem();
        LocalModTableItem localMod = localModsTable.getSelectionModel().getSelectedItem();

        if (remoteMod != null) {
            // Populate form fields from remote mod
            if (remoteMod.getPublishedFileId() != null) {
                publishedFileIdField.setText(remoteMod.getPublishedFileId().toString());
            } else {
                publishedFileIdField.clear();
            }

            nameField.setText(remoteMod.getTitle());

            if (remoteMod.getDescription() != null) {
                descriptionArea.setText(remoteMod.getDescription());
            } else {
                descriptionArea.clear();
            }

            tagsField.setText(remoteMod.getTags());

            // Clear fields that aren't applicable
            changelogArea.clear();
            contentFolderField.clear();
            previewImageField.clear();

            settingsView.appendToLog("Loaded remote mod details for editing.");
        } else if (localMod != null) {
            // Populate form fields from local mod
            if (localMod.getPublishedFileId() != null) {
                publishedFileIdField.setText(localMod.getPublishedFileId().toString());
            } else {
                publishedFileIdField.clear();
            }

            nameField.setText(localMod.getTitle());

            if (localMod.getDescription() != null) {
                descriptionArea.setText(localMod.getDescription());
            } else {
                descriptionArea.clear();
            }

            tagsField.setText(localMod.getTags());
            contentFolderField.setText(localMod.getContentFolder());
            previewImageField.setText(localMod.getPreviewImage());

            // Clear fields that aren't applicable
            changelogArea.clear();

            settingsView.appendToLog("Loaded local mod details for editing.");
        }
    }

    /**
     * Deletes the selected remote mod.
     */
    private void deleteRemoteMod() {
        RemoteModTableItem remoteMod = remoteModsTable.getSelectionModel().getSelectedItem();

        if (remoteMod != null && remoteMod.getPublishedFileId() != null) {
            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(i18nService.get("steam.deleteConfirmTitle"));
            alert.setHeaderText(i18nService.get("steam.deleteConfirmHeader"));
            alert.setContentText(i18nService.get("steam.deleteConfirmContent") + " " + remoteMod.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    settingsView.appendToLog("Deleting remote mod: " + remoteMod.getTitle());
                    settingsView.appendToLog("This functionality is not yet implemented in the Steam API.");

                    // Remove from table
                    remoteModsData.remove(remoteMod);
                }
            });
        }
    }

    /**
     * Deletes the selected local mod.
     */
    private void deleteLocalMod() {
        LocalModTableItem localMod = localModsTable.getSelectionModel().getSelectedItem();

        if (localMod != null) {
            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(i18nService.get("steam.deleteConfirmTitle"));
            alert.setHeaderText(i18nService.get("steam.deleteConfirmHeader"));
            alert.setContentText(i18nService.get("steam.deleteConfirmContent") + " " + localMod.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    settingsView.appendToLog("Deleting local mod: " + localMod.getTitle());

                    try {
                        // Delete the content folder
                        Path contentFolderPath = Paths.get(localMod.getContentFolder());
                        if (Files.exists(contentFolderPath)) {
                            // This is a simplified version - in a real implementation, 
                            // you would need to recursively delete the directory contents
                            settingsView.appendToLog("Would delete folder: " + contentFolderPath);
                            settingsView.appendToLog("This functionality is not fully implemented yet.");
                        }

                        // Remove from table
                        localModsData.remove(localMod);
                    } catch (Exception e) {
                        log.error("Failed to delete local mod", e);
                        settingsView.appendToLog("Error: " + e.getMessage());
                    }
                }
            });
        }
    }
}
