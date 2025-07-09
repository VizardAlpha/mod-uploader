package com.github.argon.moduploader.core.auth;

import com.github.argon.moduploader.core.Clearable;
import com.github.argon.moduploader.core.file.IFileService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class BearerTokenFileProvider implements Supplier<BearerToken>, Clearable {
    private BearerToken token;
    private final Path path;
    private final IFileService fileService;

    @Override
    @Nullable
    public BearerToken get() {
        if (token == null || token.isExpired()) {
            token = readToken();
        }

        return token;
    }

    @Override
    public void clear() {
        token = null;
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
            throw new AuthException("Failed to read token from " + absolutePath, e);
        }
    }
}
