package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.CommonMapper;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.modio.api.dto.*;
import com.github.argon.moduploader.core.vendor.modio.model.ModioGame;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;

@Mapper(uses = {CommonMapper.class})
public abstract class ModioMapper {

    @Inject
    IFileService fileService;

    @Mapping(target = "ownerId", source = "submittedBy.id")
    @Mapping(target = "owner", source = "submittedBy.username")
    @Mapping(target = "timeCreated", source = "dateAdded", qualifiedByName = "toInstant")
    @Mapping(target = "timeUpdated", source = "dateUpdated", qualifiedByName = "toInstant")
    abstract ModioMod.Remote map(ModioModDto data);

    abstract ModioUser map(ModioUserDto user);

    @Mapping(target = "timeCreated", source = "dateAdded", qualifiedByName = "toInstant")
    @Mapping(target = "timeUpdated", source = "dateUpdated", qualifiedByName = "toInstant")
    abstract ModioGame map(ModioGameDto game);

    public ModioEditModDto mapEdit(ModioMod.Local mod) throws VendorException {
        byte[] logo;
        try {
            logo = fileService.readBytes(mod.logo());
        } catch (IOException e) {
            throw new VendorException("Error reading mod logo from: " + mod.logo(),e);
        }

        return new ModioEditModDto(
            mod.name(),
            mod.nameId(),
            mod.summary(),
            mod.description(),
            logo,
            mod.homepageUrl(),
            mod.visible(),
            mod.maturityOptions(),
            mod.creditOptions(),
            mod.communityOptions(),
            mod.stock(),
            mod.metadataKvp(),
            mod.tags()
        );
    }

    public ModioAddModDto mapAdd(ModioMod.Local mod) throws VendorException {
        byte[] logo;
        try {
            logo = fileService.readBytes(mod.logo());
        } catch (IOException e) {
            throw new VendorException("Error reading mod logo from: " + mod.logo(),e);
        }

        return new ModioAddModDto(
            mod.name(),
            logo,
            mod.nameId(),
            mod.summary(),
            mod.description(),
            mod.homepageUrl(),
            mod.visible(),
            mod.maturityOptions(),
            mod.creditOptions(),
            mod.communityOptions(),
            mod.stock(),
            mod.metadataKvp(),
            mod.tags()
        );
    }

    @Mapping(target = "id", source = "modId")
    abstract ModioMod.Local map(Long modId, ModioMod.Local mod);
}
