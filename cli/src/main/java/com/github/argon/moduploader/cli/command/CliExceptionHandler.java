package com.github.argon.moduploader.cli.command;

import picocli.CommandLine;

public class CliExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    public int handleExecutionException(Exception ex, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        commandLine.getErr().println(ex.getMessage());
        commandLine.usage(commandLine.getErr());

        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }
}
