package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CommandLine.Command(name = "list", description = "List your published mods")
public class SteamListCommand implements Runnable {
    @Inject
    FileService fileService;

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480", required = true)
    Integer appId;

    @Override
    public void run() {
        try {
            try (Steam steam = new Steam(appId, fileService)) {
                SteamWorkshopService steamWorkshop = steam.getWorkshop();
                steamWorkshop.fetchPublishedMods((steamMods, result) -> {
                    System.out.println(result);
                    steamMods.forEach(steamMod -> {
                        System.out.println(steamMod.toString());
                    });
                });
                steam.block(Duration.of(10, ChronoUnit.SECONDS));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
