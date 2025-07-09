package com.github.argon.moduploader.core.vendor.modio.api.dto;

import com.github.argon.moduploader.core.vendor.modio.model.ModioVisibility;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

/**
 * @param name Name of your mod.
 * @param nameId Path for the mod on mod.io. For example: https://mod.io/g/gamename/m/{mod-name-id-here}. If no `name_id` is specified the `name` will be used. For example: 'Stellaris Shader Mod' will become 'stellaris-shader-mod'. Cannot exceed 50 characters.
 * @param summary Summary for your mod, giving a brief overview of what it's about. Cannot exceed 250 characters.
 * @param description Detailed description for your mod, which can include details such as 'About', 'Features', 'Install Instructions', 'FAQ', etc. HTML supported and encouraged.
 * @param logo Image file which will represent your mods logo. Must be jpg, jpeg or png format and cannot exceed 8MB in filesize. Dimensions must be at least 512x288 and we recommended you supply a high resolution image with a 16 / 9 ratio. mod.io will use this image to make three thumbnails for the dimensions 320x180, 640x360 and 1280x720.
 * @param homepageUrl Official homepage for your mod. Must be a valid URL.
 * @param visible Visibility of the mod.
 * @param maturityOptions Mature content found in this mod. Bitwise combination possible.
 * @param creditOptions Credit options enabled for this mod. Bitwise combination possible.
 * @param communityOptions Community features enabled for this mod. Bitwise combination possible.
 * @param stock Maximum number of times this mod can be sold.
 * @param metadataKvp Key value pairs you want to add where the key and value are separated by a colon ':'. Keys and values cannot exceed 255 characters in length.
 * @param tags Tags to apply to the mod.
 */
public record ModioEditModDto(
    @RestForm @NotNull @NotBlank String name,
    @RestForm @Nullable @Length(max = 50) String nameId,
    @RestForm @NotNull @Length(max = 250) String summary,
    @RestForm @Nullable String description,
    @RestForm @NotNull byte[] logo,
    @RestForm @Nullable @URL String homepageUrl,
    @RestForm @Nullable ModioVisibility visible,
    @RestForm @Nullable @Min(0) Integer maturityOptions,
    @RestForm @Nullable @Min(0) Integer creditOptions,
    @RestForm @Nullable @Min(0) Integer communityOptions,
    @RestForm @Nullable @Min(0) Integer stock,
    @RestForm @Nullable List<@Length(max = 255) String> metadataKvp,
    @RestForm @NotNull List<String> tags
) {
}
