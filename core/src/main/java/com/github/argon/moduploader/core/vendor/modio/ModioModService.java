package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.AuthException;
import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.auth.BearerTokenFileProvider;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.modio.api.ModioModsClient;
import com.github.argon.moduploader.core.vendor.modio.api.dto.*;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import jakarta.annotation.Nullable;
import jakarta.validation.Validator;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ModioModService {

    private final ModioModsClient modClient;
    private final ModioMapper mapper;
    private final IFileService fileService;
    private final BearerTokenFileProvider bearerTokenProvider;
    private final Validator validator;

    public List<ModioMod.Remote> getMods(
        String apiKey,
        Long gameId,
        @Nullable Long submittedBy,
        @Nullable String submittedByDisplayName,
        @Nullable String modName,
        @Nullable List<String> tags
    ) {
        List<ModioModDto> mods = new ArrayList<>();

        int offset = 0;
        int modsTotal = 0;

        do {
            ModioModsDto modsPage = modClient.getMods(apiKey, gameId, submittedBy, submittedByDisplayName, modName, tags, offset, null);
            mods.addAll(modsPage.data());

            modsTotal = modsPage.resultTotal();
            offset += mods.size();
        } while (mods.size() < modsTotal);

        return mods.stream()
            .map(mapper::map)
            .toList();
    }

    public List<ModioMod.Remote> getUserMods(String apiKey, Long gameId, Long userId) {
        return getMods(apiKey, gameId, userId, null, null, null);
    }

    public Optional<ModioMod.Remote> getMod(String apiKey, Long gameId, Long modId) {
        return modClient.getMod(apiKey, gameId, modId)
            .map(mapper::map);
    }

    public ModioMod.Remote upload(Long gameId, ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null || bearerToken.isExpired()) {
            throw new AuthException("Bearer token is null or expired");
        }

        // create or update mod meta-data, such as description
        Long modId = mod.id();
        ModioMod.Remote remote;
        if (modId == null) {
            remote = create(gameId, mod);
        } else {
            remote = update(gameId, mod);
        }

        modId = remote.id();
        ModioMod.Local updatedMod = mapper.map(modId, mod);

        // upload file
        addModFile(gameId, updatedMod, version, changelog);

        return remote;
    }

    public ModioMod.Remote update(Long gameId,ModioMod.Local mod) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null || bearerToken.isExpired()) {
            throw new AuthException("Bearer token is null or expired");
        }

        ModioEditModDto modioEditModDto = mapper.mapEdit(mod);
        validator.validate(modioEditModDto);
        ModioModDto modioModDto = modClient.editMod(bearerToken.toString(), gameId, mod.id(), modioEditModDto);

        return mapper.map(modioModDto);
    }

    public void addModFile(Long gameId, ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null || bearerToken.isExpired()) {
            throw new AuthException("Bearer token is null or expired");
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

            modClient.addModFile(bearerToken.toString(), gameId, mod.id(), modioAddModFileDto);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new VendorException(e);
        }
    }

    public ModioMod.Remote create(Long gameId, ModioMod.Local mod) throws VendorException {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null || bearerToken.isExpired()) {
            throw new AuthException("Bearer token is null or expired");
        }

        ModioAddModDto modioAddModDto = mapper.mapAdd(mod);
        validator.validate(modioAddModDto);
        ModioModDto modioModDto = modClient.addMod(bearerToken.toString(), gameId, modioAddModDto);

        return mapper.map(modioModDto);
    }

    public Response archive(Long gameId, Long modId) {
        BearerToken bearerToken = bearerTokenProvider.get();

        if (bearerToken == null || bearerToken.isExpired()) {
            throw new AuthException("Bearer token is null or expired");
        }

        return modClient.archiveMod(bearerToken.toString(), gameId, modId);
    }
}
