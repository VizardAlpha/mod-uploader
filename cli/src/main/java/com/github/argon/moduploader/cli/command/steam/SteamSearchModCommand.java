package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CommandLine.Command(name = "search-mod", description = "Search for mods in the Steam Workshop.")
public class SteamSearchModCommand implements Runnable {
    @Inject CliPrinter cliPrinter;
    @Inject Steam steam;

    @CommandLine.Option(names = {"-s", "--search-term"},
        description = "The text to search for.")
    String searchTerm;

    @Override
    public void run() {
        try {
            SteamWorkshopService steamWorkshop = steam.workshop();
            steamWorkshop.searchMods(searchTerm, (steamMods, result) -> {
                cliPrinter.printSteamMods(steamMods);
            });
            steam.block(Duration.of(10, ChronoUnit.SECONDS));

        } catch (Exception e) {
            // TODO better exceptions
            throw new RuntimeException(e);
        }
    }
}
