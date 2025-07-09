package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "published", description = "List your published mods")
public class SteamPublishedCommand implements Runnable {

    @Inject Steam steam;
    @Inject CliPrinter cliPrinter;

    @Override
    public void run() {
        steam.workshop().getPublishedMods((publishedMods, result)
            -> cliPrinter.printSteamMods(publishedMods));
        steam.awaits();
    }
}
