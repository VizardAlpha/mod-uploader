package com.github.argon.moduploader.vendor.modio;

import jakarta.annotation.Nullable;

import java.nio.file.Path;
import java.util.List;

public record ModioMod() {
    public record Remote(

    ){}

    public record Local(
        @Nullable Long publishedFileId,
        String title,
        @Nullable String description,
        List<String> tags,
        Path contentFolder,
        Path previewImage
    ){}
}
