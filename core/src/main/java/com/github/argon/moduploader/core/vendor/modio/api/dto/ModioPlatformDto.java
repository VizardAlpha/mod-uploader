package com.github.argon.moduploader.core.vendor.modio.api.dto;

public record ModioPlatformDto(
    String platform,
    String label,
    Boolean moderated,
    Boolean locked
) {
}
