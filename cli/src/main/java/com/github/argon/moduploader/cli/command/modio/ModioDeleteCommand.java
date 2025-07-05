package com.github.argon.moduploader.cli.command.modio;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "delete", description = "CAN NOT BE UNDONE! Will remove a mod from mod.io. ")
public class ModioDeleteCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Override
    public Integer call() {

        return 0;
    }
}
