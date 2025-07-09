package com.github.argon.moduploader.core.vendor.steam.model;

import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public record SteamMod() {
    public record Remote (
        @Nullable Long id,
        String name,
        List<String> tags,
        @Nullable String description,
        Integer fileSize,
        Integer ownerId,
        Instant timeCreated,
        Instant timeUpdated,
        Integer votesUp,
        Integer votesDown
    ) implements Serializable {}

    public record Local(
        @Nullable Long id,
        String name,
        @Nullable String description,
        List<String> tags,
        Path contentFolder,
        Path previewImage
    ){}
}
