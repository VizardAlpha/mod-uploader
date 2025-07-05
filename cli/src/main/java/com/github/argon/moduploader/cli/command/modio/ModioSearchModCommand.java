package com.github.argon.moduploader.cli.command.modio;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "search-mod", description = "Search for mods on mod.io")
public class ModioSearchModCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Override
    public Integer call() {
        if (!parentCommand.init(null)) {
            return 1;
        }

        return 0;
    }
}
