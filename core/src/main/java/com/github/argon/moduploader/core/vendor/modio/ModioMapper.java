package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioModsDto;
import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioUserDto;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;

import java.time.Instant;

public class ModioMapper {
    public static ModioMod.Remote map(ModioModsDto.Data data) {
        return new ModioMod.Remote(
            data.id(),
            data.name(),
            data.submittedBy().username(),
            data.submittedBy().id(),
            data.description(),
            data.gameId(),
            data.status(),
            data.visible(),
            (data.dateAdded() != null) ? Instant.ofEpochSecond(data.dateAdded()) : null,
            (data.dateUpdated() != null) ? Instant.ofEpochSecond(data.dateUpdated()) : null
        );
    }

    public static ModioUser map(ModioUserDto user) {
        return new ModioUser(
            user.id(),
            user.username(),
            user.nameId()
        );
    }
}
