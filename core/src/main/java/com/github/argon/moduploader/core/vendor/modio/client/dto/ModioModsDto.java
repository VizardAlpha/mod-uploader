package com.github.argon.moduploader.core.vendor.modio.client.dto;

import java.time.Instant;
import java.util.List;

public record ModioModsDto(
    List<Data> data,
    Integer resultCount,
    Integer resultOffset,
    Integer resultLimit,
    Integer resultTotal
) {
    public record Data(
        Long id,
        String name,
        String description,
        Integer gameId,
        Integer status,
        Integer visible,
        Long dateAdded,
        Long dateUpdated,
        ModFile modfile,
        List<Tag> tags,
        ModioUserDto submittedBy
    ) {}

    public record ModFile(
        Long id,
        Long modId,
        String version,
        Instant dateAdded,
        Instant dateUpdated,
        String metadataBlob
    ) {}

    public record Tag(String name) {}
}
