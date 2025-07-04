package com.github.argon.moduploader.core.vendor.steam.model;

import jakarta.annotation.Nullable;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public record SteamMod() {
    public record Remote(
        @Nullable Long publishedFileId,
        String title,
        List<String> tags,
        @Nullable String description,
        Integer fileSize,
        Integer ownerId,
        Instant timeCreated,
        Instant timeUpdated,
        Integer votesUp,
        Integer votesDown
    ){}

    public record Local(
        @Nullable Long publishedFileId,
        String title,
        @Nullable String description,
        List<String> tags,
        Path contentFolder,
        Path previewImage
    ){}
}
