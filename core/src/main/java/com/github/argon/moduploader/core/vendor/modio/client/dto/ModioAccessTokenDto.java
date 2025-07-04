package com.github.argon.moduploader.core.vendor.modio.client.dto;

import java.time.Instant;

public record ModioAccessTokenDto(
    Integer code,
    String accessToken,
    Instant dateExpires
) {}
