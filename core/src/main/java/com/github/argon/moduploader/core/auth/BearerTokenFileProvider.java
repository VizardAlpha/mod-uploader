package com.github.argon.moduploader.core.auth;

import com.github.argon.moduploader.core.file.FileService;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BearerTokenFileProvider implements Provider<BearerToken> {
    private BearerToken token;
    private final Path path;
    private final FileService fileService;

    @Override
    @Nullable
    public BearerToken get() {
        if (token == null) {
            token = readToken();
        }

        return token;
    }

    @Nullable
    private BearerToken readToken() {
        Path absolutePath = path.toAbsolutePath();

        try {
            List<String> lines = fileService.readLines(absolutePath);
            if (lines.size() != 2) {
                return null;
            }

            String expireDateString = lines.get(0);
            String tokenString = lines.get(1);
            Instant expireDate = Instant.ofEpochSecond(Long.parseLong(expireDateString));

            return new BearerToken(tokenString, expireDate); // todo implement expire;
        } catch (Exception e) {
            log.warn("Error while reading bearer token from {}", absolutePath, e);
            return null;
        }
    }
}
