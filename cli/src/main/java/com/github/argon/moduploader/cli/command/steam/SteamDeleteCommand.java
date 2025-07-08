package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.core.vendor.steam.Steam;
import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "CAN NOT BE UNDONE! Will remove a mod from the Steam Workshop. ")
public class SteamDeleteCommand implements Runnable {
    @Inject Steam steam;

    @CommandLine.Option(names = {"-id", "--mod-id"},
        description = "The Steam Workshop mod to delete.")
    Long modId;

    @Override
    public void run() {
        steam.workshop().deleteMod(modId, (deletedModId, result) -> {
            System.out.println(deletedModId);
        });
    }
}
