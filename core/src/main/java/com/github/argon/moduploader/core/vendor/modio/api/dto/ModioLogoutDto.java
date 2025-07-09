package com.github.argon.moduploader.core.vendor.modio.api.dto;

public record ModioLogoutDto(
   Integer code,
   Boolean success,
   String message
) {}
