package com.github.argon.moduploader.core.vendor.modio.client.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public record ModioErrorDto(
    Error error
) {
    public record Error(
        Integer code,
        Integer errorRef,
        String message,
        Map<String, String> errors
    ) {}
}
