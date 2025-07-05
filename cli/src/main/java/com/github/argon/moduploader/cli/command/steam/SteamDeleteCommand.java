package com.github.argon.moduploader.cli.command.steam;

import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "CAN NOT BE UNDONE! Will remove a mod from the Steam Workshop. ")
public class SteamDeleteCommand implements Runnable {
    @CommandLine.ParentCommand
    SteamCommand parentCommand;

    @Override
    public void run() {

    }
}
