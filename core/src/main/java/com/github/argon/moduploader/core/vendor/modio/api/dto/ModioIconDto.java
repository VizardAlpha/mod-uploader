package com.github.argon.moduploader.core.vendor.modio.api.dto;

public record ModioIconDto(
    String filename,
    String original,
    String thumb_64x64,
    String thumb_128x128,
    String thumb_256x256
) {}
