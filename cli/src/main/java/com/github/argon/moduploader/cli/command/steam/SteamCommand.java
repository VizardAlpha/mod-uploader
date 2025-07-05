package com.github.argon.moduploader.cli.command.steam;

import picocli.CommandLine;

@CommandLine.Command(name = "steam", mixinStandardHelpOptions = true, subcommands = {
    SteamUploadCommand.class,
    SteamListCommand.class,
    SteamSearchGameCommand.class,
    SteamSearchModCommand.class,
    SteamDeleteCommand.class
})
public class SteamCommand {
    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480",
    description = "The Steam app id of the game you want to upload a mod to.")
    Integer appId;
}
