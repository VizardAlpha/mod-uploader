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
    abstract ModioMod.Remote map(ModioModDto modDto);

    abstract ModioUser map(ModioUserDto userDto);

    @Mapping(target = "timeCreated", source = "dateAdded", qualifiedByName = "toInstant")
    @Mapping(target = "timeUpdated", source = "dateUpdated", qualifiedByName = "toInstant")
    abstract ModioGame map(ModioGameDto gameDto);

    @Mapping(target = "logo", source = "logoData")
    abstract ModioEditModDto mapEdit(byte[] logoData, ModioMod.Local mod);
    @Mapping(target = "logo", source = "logoData")
    abstract ModioAddModDto mapAdd(byte[] logoData, ModioMod.Local mod);

    public ModioEditModDto mapEdit(ModioMod.Local mod) throws VendorException {
        byte[] logoData;
        try {
            logoData = fileService.readBytes(mod.logo());
        } catch (IOException e) {
            throw new VendorException("Error reading mod logo from: " + mod.logo(), e);
        }

        return mapEdit(logoData, mod);
    }

    public ModioAddModDto mapAdd(ModioMod.Local mod) throws VendorException {
        byte[] logoData;
        try {
            logoData = fileService.readBytes(mod.logo());
        } catch (IOException e) {
            throw new VendorException("Error reading mod logo from: " + mod.logo(), e);
        }

        return mapAdd(logoData, mod);
    }

    @Mapping(target = "id", source = "modId")
    abstract ModioMod.Local map(Long modId, ModioMod.Local mod);
}
