package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.BearerTokenFileConsumer;
import com.github.argon.moduploader.core.auth.BearerTokenFileProvider;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.modio.api.ModioGameClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioModsClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioOAuthClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioUserClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.Validator;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.nio.file.Path;

@ApplicationScoped
public class ModioConfiguration {
    @Inject
    ModioProperties modioProperties;

    @Singleton
    @Produces
    public Modio modio(
        ModioModService storeService,
        ModioUserService userService,
        ModioAuthService authService,
        ModioGameService gameService
    ) {
        return new Modio(storeService, userService, authService, gameService);
    }

    @Singleton
    @Produces
    public BearerTokenFileConsumer bearerTokenFileConsumer(IFileService fileService) {
        Path tokenFilePath = modioProperties.tokenFilePath();
        return new BearerTokenFileConsumer(tokenFilePath, fileService);
    }

    @Singleton
    @Produces
    public BearerTokenFileProvider bearerTokenFileProvider(IFileService fileService) {
        Path tokenFilePath = modioProperties.tokenFilePath();
        return new BearerTokenFileProvider(tokenFilePath, fileService);
    }

    @Singleton
    @Produces
    public ModioAuthService modioAuthService(
        @RestClient ModioOAuthClient modioAuthClient,
        BearerTokenFileProvider bearerTokenProvider,
        BearerTokenFileConsumer bearerTokenConsumer
    ) {
        return new ModioAuthService(modioAuthClient, bearerTokenProvider, bearerTokenConsumer);
    }

    @Singleton
    @Produces
    public ModioUserService modioUserService(
        @RestClient ModioUserClient modioUserClient,
        ModioMapper modioMapper
    ) {
        return new ModioUserService(modioUserClient, modioMapper);
    }

    @Singleton
    @Produces
    public ModioGameService modioGameService(
        @RestClient ModioGameClient modioGameClient,
        ModioMapper modioMapper
    ) {
        return new ModioGameService(modioGameClient, modioMapper);
    }

    @Singleton
    @Produces
    public ModioModService modioStoreService(
        @RestClient ModioModsClient modioModsClient,
        ModioMapper  modioMapper,
        IFileService fileService,
        BearerTokenFileProvider bearerTokenProvider,
        Validator validator
    ) {
        return new ModioModService(modioModsClient, modioMapper, fileService, bearerTokenProvider, validator);
    }
}
