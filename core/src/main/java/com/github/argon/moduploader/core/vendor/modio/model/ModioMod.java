package com.github.argon.moduploader.core.vendor.modio.model;

import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioVisibility;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

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
        Instant timeCreated,
        Instant timeUpdated
    ){}

    public record Local(
        @Nullable Long id,
        String name,
        @Nullable String nameId,
        String summary,
        @Nullable String description,
        Path logo,
        @NotNull Path contentFolder,
        String homepageUrl,
        @Nullable ModioVisibility visible,
        @Nullable Integer maturityOptions,
        @Nullable Integer creditOptions,
        @Nullable Integer communityOptions,
        @Nullable Integer stock,
        @Nullable List<String> metadataKvp,
        @Nullable List<String> tags

    ){}
}
