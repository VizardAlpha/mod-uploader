package com.github.argon.moduploader.cli.command.steam;

import picocli.CommandLine;

@CommandLine.Command(name = "steam", mixinStandardHelpOptions = true, subcommands = {
    SteamUploadCommand.class,
    SteamListCommand.class,
})
public class SteamCommand {
}
