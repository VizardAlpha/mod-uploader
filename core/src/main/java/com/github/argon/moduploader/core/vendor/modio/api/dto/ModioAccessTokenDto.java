package com.github.argon.moduploader.core.vendor.modio.api.dto;

import java.time.Instant;

public record ModioAccessTokenDto(
    Integer code,
    String accessToken,
    Instant dateExpires
) {}
