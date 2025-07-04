package com.github.argon.moduploader.cli.command.modio;

import picocli.CommandLine;

@CommandLine.Command(name = "modio", mixinStandardHelpOptions = true, subcommands = {
    ModioLoginCommand.class,
    ModioListCommand.class,
    ModioUploadCommand.class
})
public class ModioCommand {

}
