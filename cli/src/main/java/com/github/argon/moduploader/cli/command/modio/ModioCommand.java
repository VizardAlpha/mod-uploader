package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.Initializable;
import com.github.argon.moduploader.core.auth.BearerTokenFileConsumer;
import com.github.argon.moduploader.core.auth.BearerTokenFileProvider;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.modio.*;
import com.github.argon.moduploader.core.vendor.modio.api.ModioModsClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioOAuthClient;
import com.github.argon.moduploader.core.vendor.modio.api.ModioUserClient;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "modio", mixinStandardHelpOptions = true, subcommands = {
    ModioLoginCommand.class,
    ModioListCommand.class,
    ModioUploadCommand.class,
    ModioSearchModCommand.class,
    ModioSearchGameCommand.class,
    ModioDeleteCommand.class,
})
public class ModioCommand implements Initializable<Void> {
    @RestClient
    ModioModsClient modioModsClient;

    @RestClient
    ModioUserClient modioUserClient;

    @RestClient
    ModioOAuthClient modioAuthClient;

    @Inject
    IFileService fileService;

    @Inject
    ModioProperties modioProperties;

    @Inject
    ModioMapper modioMapper;

    @Inject
    Validator validator;

    @CommandLine.Option(names = {"-key", "--api-key"}, required = true,
        description = "The mod.io api access key. You can create a key in your mod.io profile")
    String apiKey;

    @CommandLine.Option(names = {"-gid", "--game-id"}, defaultValue = "0",
        description = "Id of the game you want to interact")
    Long gameId;

    Modio modio;

    @Override
    public boolean init(Void unused) {
        if (modio != null) {
            return true;
        }

        String apiKey = getApiKey();

        if (apiKey == null) {
            System.err.println("""
                No modio.api-key found in application.properties.
                You have to specify the key via '-key=1234'""");
            return false;
        }
        Path tokenFilePath = modioProperties.tokenFilePath();
        BearerTokenFileProvider bearerTokenProvider = new BearerTokenFileProvider(tokenFilePath, fileService);
        BearerTokenFileConsumer bearerTokenFileConsumer = new BearerTokenFileConsumer(tokenFilePath, fileService);

        ModioStoreService storeService = new ModioStoreService(apiKey, gameId, modioModsClient, modioMapper, fileService, bearerTokenProvider, validator);
        ModioUserService userService = new ModioUserService(modioUserClient, modioMapper);
        ModioAuthService authService = new ModioAuthService(apiKey, modioAuthClient, bearerTokenProvider, bearerTokenFileConsumer);

        modio = new Modio(storeService, userService, authService);

        return true;
    }

    private String getApiKey() {
        if (apiKey == null) {
            apiKey = modioProperties.apiKey().orElse(null);
        }

        return apiKey;
    }
}
