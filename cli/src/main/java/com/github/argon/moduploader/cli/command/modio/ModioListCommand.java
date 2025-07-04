package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.auth.BearerTokenFileProvider;
import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.vendor.modio.ModioProperties;
import com.github.argon.moduploader.core.vendor.modio.ModioStoreService;
import com.github.argon.moduploader.core.vendor.modio.ModioUserService;
import com.github.argon.moduploader.core.vendor.modio.client.ModioModsClient;
import com.github.argon.moduploader.core.vendor.modio.client.ModioUserClient;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "list", description = "List your published mods.")
public class ModioListCommand implements Callable<Integer> {
    @RestClient
    ModioModsClient modioModsClient;

    @RestClient
    ModioUserClient modioUserClient;

    @Inject
    FileService fileService;

    @Inject
    ModioProperties modioProperties;

    @Inject
    ModioLoginCommand loginCommand;

    // TODO make this global for all modio commands
    @CommandLine.Option(names = {"-key", "--api-key"}, required = true,
        description = "The mod.io api access key. You can create a key in your mod.io profile")
    String apiKey;

    @CommandLine.Option(names = {"-app", "--app-id"}, required = true)
    Long gameId;

    @CommandLine.Option(names = {"-oid", "--owner-id"})
    Long ownerId;

    @Override
    public Integer call() {
        if (apiKey == null) {
            apiKey = modioProperties.apiKey().orElse(null);
        }

        if (apiKey == null) {
            System.err.println("""
                No modio.api-key found in application.properties.
                You have to specify the key via '-key=1234'""");
            return 1;
        }

        ModioStoreService modioStore = new ModioStoreService(apiKey, gameId, modioModsClient);

        if (ownerId == null) {
            Path tokenFilePath = modioProperties.tokenFilePath();
            BearerTokenFileProvider bearerTokenProvider = new BearerTokenFileProvider(tokenFilePath, fileService);
            BearerToken bearerToken = bearerTokenProvider.get();

            // force login
            while (bearerToken == null || bearerToken.isExpired()) {
                new CommandLine(loginCommand).execute("-key=%s".formatted(apiKey), "-e");
                bearerToken = bearerTokenProvider.get();
            }

            ModioUserService userService = new ModioUserService(modioUserClient);
            ModioUser user = userService.getUser(bearerToken);

            ownerId = user.id();
        }

        modioStore.fetchPublishedMods(ownerId)
            .forEach(mod -> {
                System.out.printf("name=%s | id=%s | owner=%s | ownerId=%s | dateUpdated=%s\n",
                    mod.name(), mod.id(), mod.owner(), mod.ownerId(), mod.dateUpdated());
            });


        return 0;
    }
}
