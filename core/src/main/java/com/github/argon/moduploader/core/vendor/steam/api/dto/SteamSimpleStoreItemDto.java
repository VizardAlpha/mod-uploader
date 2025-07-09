package com.github.argon.moduploader.core.vendor.steam.api.dto;

public record SteamSimpleStoreItemDto(
    Integer appid,
    String name,
    Integer lastModified,
    Integer priceChangeNumber
) {
}
