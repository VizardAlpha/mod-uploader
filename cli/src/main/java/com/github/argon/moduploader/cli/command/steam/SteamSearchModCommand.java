package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import jakarta.inject.Inject;
import picocli.CommandLine;

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
            steam.workshop().searchMods(searchTerm,
                (foundMods, result) -> cliPrinter.printSteamMods(foundMods));
            steam.awaits();
        } catch (Exception e) {
            // TODO better exceptions
            throw new RuntimeException(e);
        }
    }
}
