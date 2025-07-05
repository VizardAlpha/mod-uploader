package com.github.argon.moduploader.core.vendor.modio.api.dto;

import java.util.List;

public record ModioModDto(
    Long id,
    String name,
    String nameId,
    String summary,
    String description,
    Integer gameId,
    Integer status,
    String profileUrl,
    String homepageUrl,
    List<ModioTagDto> tags,
    ModioVisibility visible,
    Long dateAdded,
    Long dateUpdated,
    Long dateLive,
    Integer maturityOptions,
    Integer creditOptions,
    Integer communityOptions,
    Integer stock,
    ModioModFileDto modfile,
    ModioLogoDto logo,
    ModioUserDto submittedBy,
    ModioStatsDto stats
) {
}
