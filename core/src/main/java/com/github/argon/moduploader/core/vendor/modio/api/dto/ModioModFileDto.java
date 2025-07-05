package com.github.argon.moduploader.core.vendor.modio.api.dto;

import java.time.Instant;

public record ModioModFileDto(
    Long id,
    Long modId,
    String version,
    Instant dateAdded,
    Instant dateUpdated
) {
}
