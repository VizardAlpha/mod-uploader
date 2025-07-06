package com.github.argon.moduploader.core.vendor.modio.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Map;

@RegisterForReflection
public record ModioApiErrorDto(
    Error error
) {
    public record Error(
        Integer code,
        Integer errorRef,
        String message,
        Map<String, String> errors
    ) {}
}
