package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.AuthException;
import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.modio.api.ModioModsClient;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioAddModDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioAddModFileDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioEditModDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioModDto;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ModioStoreService {

    private final String apiKey;
    private final Long gameId;
    private final ModioModsClient modioClient;
    private final ModioMapper mapper;
    private final IFileService fileService;
    private final Provider<BearerToken> bearerTokenProvider;
    private final Validator validator;

    public List<ModioMod.Remote> getMods(@Nullable Long userId) {
        // todo paging
        return modioClient.getMods(apiKey, gameId, userId)
            .data().stream()
            .map(mapper::map)
            .toList();
    }

    public ModioMod.Remote upload(ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null) {
            throw new AuthException("Bearer token is null");
        }

        // create or update mod meta-data, such as description
        Long modId = mod.id();
        ModioMod.Remote remote;
        if (modId == null) {
            remote = create(mod);
        } else {
            remote = update(mod);
        }

        modId = remote.id();
        ModioMod.Local updatedMod = mapper.map(modId, mod);

        // upload file
        addModFile(updatedMod,  version, changelog);

        return remote;
    }

    public ModioMod.Remote update(ModioMod.Local mod) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null) {
            throw new AuthException("Bearer token is null");
        }

        ModioEditModDto modioEditModDto = mapper.mapEdit(mod);
        validator.validate(modioEditModDto);
        ModioModDto modioModDto = modioClient.editMod(bearerToken.toString(), gameId, mod.id(), modioEditModDto);

        return mapper.map(modioModDto);
    }

    public void addModFile(ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null) {
            throw new AuthException("Bearer token is null");
        }

        // TODO this will eat up to 500MB memory... implement multi part upload
        try {
            Path zipPath = fileService.zip(mod.contentFolder());
            UUID uploadId = UUID.randomUUID();

            byte[] data = Files.readAllBytes(zipPath);
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            String checksum = new BigInteger(1, hash).toString(16);

            ModioAddModFileDto modioAddModFileDto = new ModioAddModFileDto(
                data,
                uploadId.toString(),
                true,
                checksum,
                version,
                changelog,
                null // TODO implement platforms?
            );

            modioClient.addModFile(bearerToken.toString(), gameId, mod.id(), modioAddModFileDto);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new VendorException(e);
        }
    }

    public ModioMod.Remote create(ModioMod.Local mod) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null) {
            throw new AuthException("Bearer token is null");
        }

        ModioAddModDto modioAddModDto = mapper.mapAdd(mod);
        validator.validate(modioAddModDto);
        ModioModDto modioModDto = modioClient.addMod(bearerToken.toString(), gameId, modioAddModDto);

        return mapper.map(modioModDto);
    }
}
