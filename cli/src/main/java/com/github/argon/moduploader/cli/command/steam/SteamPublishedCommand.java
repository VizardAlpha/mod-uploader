package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CommandLine.Command(name = "published", description = "List your published mods")
public class SteamPublishedCommand implements Runnable {

    @Inject Steam steam;
    @Inject CliPrinter cliPrinter;

    @Override
    public void run() {
        try {
            SteamWorkshopService steamWorkshop = steam.workshop();
            steamWorkshop.getPublishedMods((steamMods, result) -> {
                cliPrinter.printSteamMods(steamMods);
            });
            steam.block(Duration.of(10, ChronoUnit.SECONDS));
        } catch (Exception e) {
            // TODO better exceptions
            throw new RuntimeException(e);
        }
    }
}
