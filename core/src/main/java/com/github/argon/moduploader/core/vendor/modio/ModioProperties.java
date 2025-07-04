package com.github.argon.moduploader.core.vendor.modio;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.nio.file.Path;
import java.util.Optional;

@StaticInitSafe
@ConfigMapping(prefix = "modio")
public interface ModioProperties {

    Optional<String> apiKey();
    @WithDefault("modio.token")
    Path tokenFilePath();
}
