# Quarkus FX Example Application

This application is an example of JavaFX integration with Quarkus, using the `quarkus-fx` extension.

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/github/argon/moduploader/ui/
│   │   │       ├── controller/      # JavaFX Controllers
│   │   │       ├── service/         # Business Services
│   │   │       ├── ExampleFxApplication.java  # Main Class
│   │   │       └── UiInitializer.java         # UI Initializer
│   │   ├── docker/                  # Docker configuration
│   │   └── resources/
│   │       ├── fxml/                # FXML Files
│   │       ├── i18n/                # Internationalization Files
│   │       ├── icons/               # Icon Resources
│   │       ├── styles/              # CSS Files
│   │       └── application.properties  # Configuration
├── pom.xml  # Maven Configuration
└── README.md  # This file
```

## How to Run the Application

### Method 1: Development Mode

```bash
./mvnw quarkus:dev
```

This command launches Quarkus in development mode, with hot reloading of resources (CSS, FXML).

### Method 2: Compile and Run

```bash
# Compile the application
./mvnw package

# Run the application
java -jar target/quarkus-app/quarkus-run.jar
```

### *Run with ui configuration
Edit the ui configuration and add an argument to launch directly.
```
-Dquarkus.args=ui
```

## Features

- JavaFX integration with Quarkus
- Dependency injection in JavaFX controllers
- Hot reloading of CSS stylesheets in development mode
- Internationalization support (English and French)
- Modular structure with separation of concerns
- Lombok integration for reduced boilerplate code

## Technical Details

- Java 21
- Quarkus 3.24.2
- Quarkus FX Extension 0.9.1
- Lombok 1.18.38

## Customization

You can modify the following properties in `application.properties`:

- `quarkus.fx.views-root`: Root directory for FX views
- `quarkus.fx.stylesheet-reload-strategy`: Stylesheet reload strategy (always, dev, never)
- `quarkus.fx.source-resources`: Location of source resources
- `quarkus.fx.target-resources`: Location of target resources

## Note on Native Mode

Native compilation is not supported by this extension at the moment.
