package com.github.argon.moduploader.core.vendor.steam;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

import java.util.Optional;

/**
 * Configuration for the steam vendor
 * Read from the application.properties file.
 */
@StaticInitSafe
@ConfigMapping(prefix = "steam")
public interface SteamProperties {
    Optional<String> apiKey();
}
