package com.github.argon.moduploader.core.vendor.modio.client.dto;

public record LogoutDto(
   Integer code,
   Boolean success,
   String message
) {}
