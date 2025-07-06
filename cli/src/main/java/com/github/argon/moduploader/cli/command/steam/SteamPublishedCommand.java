package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamMapper;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import com.github.argon.moduploader.core.vendor.steam.api.SteamStoreClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CommandLine.Command(name = "published", description = "List your published mods")
public class SteamPublishedCommand implements Runnable {
    @CommandLine.ParentCommand
    SteamCommand parentCommand;

    @Inject IFileService fileService;
    @Inject SteamMapper mapper;
    @Inject CliPrinter cliPrinter;
    @RestClient SteamStoreClient storeClient;

    @Override
    public void run() {
        Integer appId = parentCommand.appId;

        try {
            try (Steam steam = new Steam(appId, fileService, storeClient, mapper)) {
                SteamWorkshopService steamWorkshop = steam.getWorkshop();
                steamWorkshop.getPublishedMods((steamMods, result) -> {
                    cliPrinter.printSteamMods(steamMods);
                });
                steam.block(Duration.of(10, ChronoUnit.SECONDS));
            }

        } catch (Exception e) {
            // TODO better exceptions
            throw new RuntimeException(e);
        }
    }
}
