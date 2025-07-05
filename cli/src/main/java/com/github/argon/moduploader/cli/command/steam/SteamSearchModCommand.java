package com.github.argon.moduploader.cli.command.steam;

import picocli.CommandLine;

@CommandLine.Command(name = "search-mod", description = "Search for mods in the Steam Workshop.")
public class SteamSearchModCommand implements Runnable {
    @CommandLine.ParentCommand
    SteamCommand parentCommand;

    @Override
    public void run() {

    }
}
