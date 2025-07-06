package com.github.argon.moduploader.core.vendor.modio.api.dto;

import com.github.argon.moduploader.core.vendor.modio.model.ModioTargetPlatforms;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

/**
 *
 * @param fileData Required if the uploadId parameter is omitted. The binary file for the release.
 * @param uploadId Required if the filedata parameter is omitted. The UUID of a completed upload session.
 * @param active Flag this upload as the current release.
 * @param fileHash MD5 of the submitted file. When supplied the MD5 will be compared against the uploaded files MD5.
 * @param version Version of the file release (recommended format 1.0.0 - MAJOR.MINOR.PATCH).
 * @param changelog Changelog of this release.
 * @param platforms If platform filtering enabled. An array containing one or more platforms this file is targeting.
 */
public record ModioAddModFileDto(
    @RestForm @NotNull byte[] filedata,
    @RestForm @NotNull @NotBlank String uploadId,
    @RestForm Boolean active,
    @RestForm @Nullable String fileHash,
    @RestForm @Nullable String version,
    @RestForm @Nullable String changelog,
    @RestForm @Nullable List<ModioTargetPlatforms> platforms
) {}
