package com.github.argon.moduploader.core.vendor.modio.client;

import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioErrorDto;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ModioApiException extends RuntimeException {
    @Nullable
    private final ModioErrorDto error;
    private final Response response;
}
