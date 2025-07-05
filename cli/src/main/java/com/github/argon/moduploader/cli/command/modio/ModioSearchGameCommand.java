package com.github.argon.moduploader.cli.command.modio;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "search-game", description = "Search for games on mod.io")
public class ModioSearchGameCommand implements Callable<Integer> {
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
