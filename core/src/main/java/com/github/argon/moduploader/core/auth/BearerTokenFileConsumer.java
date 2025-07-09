package com.github.argon.moduploader.core.auth;

import com.github.argon.moduploader.core.Clearable;
import com.github.argon.moduploader.core.file.IFileService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BearerTokenFileConsumer implements Consumer<BearerToken>, Clearable {
    private final Path path;
    private final IFileService fileService;

    @Override
    public void clear() {
        try {
            fileService.write(path, "");
        } catch (IOException e) {
            throw new AuthException("Error writing bearer token into " + path, e);
        }
    }

    @Override
    public void accept(BearerToken bearerToken) {
        Long dateExpires = bearerToken.getDateExpires().getEpochSecond();
        String tokenString = bearerToken.getTokenString();

        try {
            fileService.write(path, dateExpires + "\n" + tokenString);
        } catch (IOException e) {
            throw new AuthException("Error writing bearer token into " + path, e);
        }
    }
}
