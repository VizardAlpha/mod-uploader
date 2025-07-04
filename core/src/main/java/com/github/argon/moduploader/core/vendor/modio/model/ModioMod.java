package com.github.argon.moduploader.core.vendor.modio.model;

import jakarta.annotation.Nullable;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public record ModioMod() {
    public record Remote(
        Long id,
        String name,
        String owner,
        Long ownerId,
        String description,
        Integer gameId,
        Integer status,
        Integer visible,
        Instant dateAdded,
        Instant dateUpdated
    ){}

    public record Local(
        @Nullable Long id,
        String title,
        @Nullable String description,
        List<String> tags,
        Path contentFolder,
        Path previewImage
    ){}
}
