package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.file.IFileService;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamMapper;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import com.github.argon.moduploader.core.vendor.steam.model.SteamMod;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480", required = true)
    Integer appId;

    @Override
    public void run() {
        try {
            try (Steam steam = new Steam(appId, fileService, mapper)) {
                SteamWorkshopService steamWorkshop = steam.getWorkshop();
                steamWorkshop.fetchPublishedMods((steamMods, result) -> {
                    printResult(steamMods);
                });
                steam.block(Duration.of(10, ChronoUnit.SECONDS));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printResult(List<SteamMod .Remote> mods) {
        cliPrinter.printTable(mods, mod -> new String[]{
            mod.id().toString(),
            mod.name(),
            mod.ownerId().toString(),
            mod.timeUpdated().toString(),
        }, "id", "name", "ownerId", "timeUpdated");
    }
}
