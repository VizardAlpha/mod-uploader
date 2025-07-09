package com.github.argon.moduploader.core.vendor.modio.api.dto;

public record ModioLogoDto(
    String filename,
    String original,
    String thumb_320x180,
    String thumb_640x340,
    String thumb_1280x720
) {}
