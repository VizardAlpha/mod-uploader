package com.github.argon.moduploader.core.vendor.modio.api.dto;

import java.util.List;

public record ModioGameDto(
    Long id,
    Integer status,
    String name,
    String nameId,
    String summary,
    String description,
    String profileUrl,
    String instructions,
    String instructionsUrl,
    List<ModioUrlDto> otherUrls,
    List<ModioTagOptionDto> tagOptions,
    Long dateAdded,
    Long dateUpdated,
    Long dateLive,
    Integer maturityOptions,
    ModioIconDto icon,
    ModioLogoDto logo,
    ModioUserDto submittedBy,
    ModioStatsDto stats,
    List<ModioPlatformDto> platforms
) {
}
