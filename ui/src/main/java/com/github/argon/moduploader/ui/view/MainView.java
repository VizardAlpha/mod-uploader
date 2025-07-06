package com.github.argon.moduploader.ui.view;

import com.github.argon.moduploader.ui.config.ConfigService;
import com.github.argon.moduploader.ui.i18n.I18nService;
import com.github.argon.moduploader.ui.scene.SceneManager;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Main view of the application.
 * Contains tabs for Steam Workshop, Mod.io, and Settings.
 */
@Slf4j
public class MainView {

    private final ConfigService configService;
    private final I18nService i18nService;
    private final SceneManager sceneManager;

    @Getter
    private final BorderPane root;

    private TabPane tabPane;
    private SteamWorkshopView steamWorkshopView;
    private ModioView modioView;
    private SettingsView settingsView;

    /**
     * Creates a new MainView.
     *
     * @param configService the configuration service
     * @param i18nService the internationalization service
     * @param sceneManager the scene manager
     */
    public MainView(ConfigService configService, I18nService i18nService, SceneManager sceneManager) {
        this.configService = configService;
        this.i18nService = i18nService;
        this.sceneManager = sceneManager;

        // Create the root pane
        this.root = new BorderPane();

        // Initialize the UI
        initialize();
    }

    /**
     * Initializes the UI components.
     */
    private void initialize() {
        // Create the tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create the Settings view first (needed by other views)
        Stage stage = new Stage(); // Create a temporary stage for the settings view
        settingsView = new SettingsView(configService, i18nService, stage);

        // Create the Steam Workshop tab
        steamWorkshopView = new SteamWorkshopView(configService, i18nService, settingsView);
        Tab steamTab = new Tab(i18nService.get("main.tab.steam"), steamWorkshopView.getRoot());
        tabPane.getTabs().add(steamTab);

        // Create the Mod.io tab
        modioView = new ModioView(configService, i18nService);
        Tab modioTab = new Tab(i18nService.get("main.tab.modio"), modioView.getRoot());
        tabPane.getTabs().add(modioTab);

        // Create the Settings tab
        Tab settingsTab = new Tab(i18nService.get("main.tab.settings"));
        settingsTab.setContent(settingsView.getRoot());
        tabPane.getTabs().add(settingsTab);

        // Set the tab pane as the center of the root pane
        root.setCenter(tabPane);

        // Create a status bar
        Label statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-bar");
        root.setBottom(statusLabel);

        log.info("MainView initialized");
    }


    /**
     * Gets the primary stage of the application.
     *
     * @return the primary stage
     */
    public Stage getPrimaryStage() {
        return (Stage) root.getScene().getWindow();
    }
}
