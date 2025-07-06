package com.github.argon.moduploader.ui.view;

import com.github.argon.moduploader.ui.config.ConfigService;
import com.github.argon.moduploader.ui.i18n.I18nService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View for the settings dialog.
 */
@Slf4j
public class SettingsView {

    private final ConfigService configService;
    private final I18nService i18nService;
    private final Stage stage;

    @Getter
    private final VBox root;

    private ComboBox<String> languageComboBox;
    private ComboBox<String> themeComboBox;
    private ComboBox<String> logLevelComboBox;
    private TextArea logTextArea;
    private ObservableList<String> logEntries = FXCollections.observableArrayList();
    private FilteredList<String> filteredLogEntries;

    /**
     * Creates a new SettingsView.
     *
     * @param configService the configuration service
     * @param i18nService the internationalization service
     * @param stage the stage for this view
     */
    public SettingsView(ConfigService configService, I18nService i18nService, Stage stage) {
        this.configService = configService;
        this.i18nService = i18nService;
        this.stage = stage;

        // Create the root pane
        this.root = new VBox(20);
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

        // Language setting
        Label languageLabel = new Label(i18nService.get("settings.language"));
        languageLabel.getStyleClass().add("subheader");

        languageComboBox = new ComboBox<>();
        Map<String, String> languages = i18nService.getSupportedLanguages();
        languages.forEach((code, name) -> languageComboBox.getItems().add(name));

        // Set the current language
        String currentLanguage = i18nService.getCurrentLanguage().get();
        String currentLanguageName = languages.get(currentLanguage);
        languageComboBox.setValue(currentLanguageName);

        formGrid.add(languageLabel, 0, 0);
        formGrid.add(languageComboBox, 1, 0);

        // Theme setting
        Label themeLabel = new Label(i18nService.get("settings.theme"));
        themeLabel.getStyleClass().add("subheader");

        themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(
                i18nService.get("settings.theme.steamDark"),
                i18nService.get("settings.theme.steamLight")
        );

        // Set the current theme
        String currentTheme = configService.getConfig().getTheme();
        if ("steam-dark".equals(currentTheme)) {
            themeComboBox.setValue(i18nService.get("settings.theme.steamDark"));
        } else if ("steam-light".equals(currentTheme)) {
            themeComboBox.setValue(i18nService.get("settings.theme.steamLight"));
        }

        formGrid.add(themeLabel, 0, 1);
        formGrid.add(themeComboBox, 1, 1);

        // Log Level setting
        Label logLevelLabel = new Label(i18nService.get("settings.logLevel"));
        logLevelLabel.getStyleClass().add("subheader");

        logLevelComboBox = new ComboBox<>();
        logLevelComboBox.getItems().addAll(
                "ALL",
                "SEVERE",
                "WARNING",
                "INFO",
                "CONFIG",
                "FINE",
                "FINER",
                "FINEST"
        );
        logLevelComboBox.setValue("INFO");
        logLevelComboBox.setOnAction(event -> filterLogs());

        formGrid.add(logLevelLabel, 0, 2);
        formGrid.add(logLevelComboBox, 1, 2);

        // Log display
        Label logLabel = new Label(i18nService.get("settings.logs"));
        logLabel.getStyleClass().add("header");

        // Initialize log entries
        loadLogEntries();
        filteredLogEntries = new FilteredList<>(logEntries);

        // Create log text area
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setPrefRowCount(15);
        logTextArea.setPrefColumnCount(80);
        VBox.setVgrow(logTextArea, Priority.ALWAYS);

        // Update log text area with filtered entries
        updateLogTextArea();

        // Buttons
        Button saveButton = new Button(i18nService.get("settings.save"));
        saveButton.getStyleClass().add("primary");
        saveButton.setOnAction(event -> saveSettings());

        Button cancelButton = new Button(i18nService.get("settings.reset"));
        cancelButton.setOnAction(event -> resetSettings());

        Button refreshButton = new Button(i18nService.get("settings.refreshLogs"));
        refreshButton.setOnAction(event -> refreshLogs());

        HBox buttonBox = new HBox(10, saveButton, cancelButton, refreshButton);

        // Add components to the root pane
        root.getChildren().addAll(
                formGrid,
                new Separator(),
                logLabel,
                logTextArea,
                buttonBox
        );

        log.info("SettingsView initialized");
    }

    /**
     * Saves the settings and closes the dialog.
     */
    private void saveSettings() {
        // Save language setting
        String selectedLanguageName = languageComboBox.getValue();
        String selectedLanguageCode = null;
        for (var entry : i18nService.getSupportedLanguages().entrySet()) {
            if (entry.getValue().equals(selectedLanguageName)) {
                selectedLanguageCode = entry.getKey();
                break;
            }
        }

        if (selectedLanguageCode != null) {
            i18nService.changeLanguage(selectedLanguageCode);
        }

        // Save theme setting
        String selectedTheme = themeComboBox.getValue();
        if (i18nService.get("settings.theme.steamDark").equals(selectedTheme)) {
            configService.updateTheme("steam-dark");
        } else if (i18nService.get("settings.theme.steamLight").equals(selectedTheme)) {
            configService.updateTheme("steam-light");
        }

        // Close the dialog
        stage.close();

        log.info("Settings saved");
    }

    /**
     * Resets the settings to their default values.
     */
    private void resetSettings() {
        // Reset language to English
        String englishName = i18nService.getSupportedLanguages().get("en");
        languageComboBox.setValue(englishName);

        // Reset theme to Steam Dark
        themeComboBox.setValue(i18nService.get("settings.theme.steamDark"));

        // Reset log level to INFO
        logLevelComboBox.setValue("INFO");
        filterLogs();

        log.info("Settings reset to defaults");
    }

    /**
     * Loads log entries from the log file.
     */
    private void loadLogEntries() {
        logEntries.clear();

        try {
            // Try to find log files in common locations
            List<Path> logFiles = new ArrayList<>();

            // Check current directory
            Path currentDirLog = Paths.get("logs", "app.log");
            if (Files.exists(currentDirLog)) {
                logFiles.add(currentDirLog);
            }

            // Check user home directory
            Path userHomeLog = Paths.get(System.getProperty("user.home"), ".moduploader", "logs", "app.log");
            if (Files.exists(userHomeLog)) {
                logFiles.add(userHomeLog);
            }

            // If no log files found, add a message
            if (logFiles.isEmpty()) {
                logEntries.add("No log files found.");
                return;
            }

            // Read log files
            for (Path logFile : logFiles) {
                List<String> lines = Files.readAllLines(logFile);
                logEntries.addAll(lines);
            }

            // If no log entries found, add a message
            if (logEntries.isEmpty()) {
                logEntries.add("No log entries found.");
            }
        } catch (IOException e) {
            log.error("Failed to load log entries", e);
            logEntries.add("Error loading log entries: " + e.getMessage());
        }
    }

    /**
     * Updates the log text area with filtered entries.
     */
    private void updateLogTextArea() {
        logTextArea.clear();

        for (String entry : filteredLogEntries) {
            logTextArea.appendText(entry + "\n");
        }
    }

    /**
     * Filters log entries based on the selected log level.
     */
    private void filterLogs() {
        String selectedLevel = logLevelComboBox.getValue();

        filteredLogEntries.setPredicate(entry -> {
            // If ALL is selected, show all entries
            if ("ALL".equals(selectedLevel)) {
                return true;
            }

            // Check if the entry contains the selected level
            return entry.contains(selectedLevel);
        });

        updateLogTextArea();
    }

    /**
     * Refreshes the log entries.
     */
    private void refreshLogs() {
        loadLogEntries();
        filterLogs();
    }

    /**
     * Appends text to the log text area.
     * 
     * @param text The text to append
     */
    public void appendToLog(String text) {
        if (text != null && !text.isEmpty()) {
            logTextArea.appendText(text + "\n");
        }
    }
}
