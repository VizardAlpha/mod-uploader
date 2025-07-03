package com.github.argon.moduploader.command.steam;

import com.github.argon.moduploader.command.CurrentWorkingDir;
import com.github.argon.moduploader.vendor.steam.Steam;
import com.github.argon.moduploader.vendor.steam.SteamWorkshop;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@CommandLine.Command(name = "steam-list", description = "List your published mods.")
public class SteamListCommand implements Runnable {

    @Inject
    CurrentWorkingDir currentWorkingDir;

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480", required = true)
    Integer appId;

    @Override
    public void run() {
        try {
            Path currentWorkingDirPath = currentWorkingDir.getPath();
            try (Steam steam = new Steam(appId, currentWorkingDirPath)) {
                SteamWorkshop steamWorkshop = steam.getWorkshop();
                steamWorkshop
                    .fetchPublishedMods((steamMods, result) -> {
                        System.out.println(result);
                        steamMods.forEach(steamMod -> {
                            System.out.println(steamMod.toString());
                        });
                    })
                    .block(Duration.of(10, ChronoUnit.SECONDS));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
