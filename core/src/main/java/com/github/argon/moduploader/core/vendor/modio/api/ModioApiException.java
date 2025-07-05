package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioErrorDto;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ModioApiException extends RuntimeException {
    @Nullable
    private final ModioErrorDto error;
    private final Response.Status code;
    private final Response response;
}
