package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioApiErrorDto;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Thrown by any Modio Client when the mod.io returns 4xx or 5xx code.
 * Contains the parsed JSON api error object (when present),
 * the response status code and the {@link Response} itself.
 */
@Getter
@RequiredArgsConstructor
public class ModioApiException extends RuntimeException {
    @Nullable
    private final ModioApiErrorDto error;
    private final Response.Status code;
    private final Response response;
}
