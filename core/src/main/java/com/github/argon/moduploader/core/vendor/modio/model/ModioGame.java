package com.github.argon.moduploader.core.vendor.modio.model;

import java.time.Instant;

public record ModioGame(
    Long id,
    ModioStatus status,
    String name,
    String nameId,
    String summary,
    String description,
    String profileUrl,
    String instructions,
    String instructionsUrl,
    Instant timeCreated,
    Instant timeUpdated
) {
}
