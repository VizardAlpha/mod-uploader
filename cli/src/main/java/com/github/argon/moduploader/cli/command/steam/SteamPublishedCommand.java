package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamMapper;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CommandLine.Command(name = "published", description = "List your published mods")
public class SteamPublishedCommand implements Runnable {
    @CommandLine.ParentCommand
    SteamCommand parentCommand;

    @Inject
    IFileService fileService;

    @Inject
    SteamMapper mapper;

    @Inject
    CliPrinter cliPrinter;

    @Override
    public void run() {
        Integer appId = parentCommand.appId;

        try {
            try (Steam steam = new Steam(appId, fileService, mapper)) {
                SteamWorkshopService steamWorkshop = steam.getWorkshop();
                steamWorkshop.fetchPublishedMods((steamMods, result) -> {
                    cliPrinter.printSteamMods(steamMods);
                });
                steam.block(Duration.of(10, ChronoUnit.SECONDS));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
