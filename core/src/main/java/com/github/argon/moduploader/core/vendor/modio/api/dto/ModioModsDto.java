package com.github.argon.moduploader.core.vendor.modio.api.dto;

import java.util.List;

public record ModioModsDto(
    List<ModioModDto> data,
    Integer resultCount,
    Integer resultOffset,
    Integer resultLimit,
    Integer resultTotal
) {}
