package com.github.argon.moduploader.command;

import com.github.argon.moduploader.command.steam.SteamUploadCommand;
import com.github.argon.moduploader.command.steam.SteamListCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {
    SteamUploadCommand.class,
    SteamListCommand.class
})
public class EntryCommand implements Runnable, QuarkusApplication {

    @Inject
    CommandLine.IFactory cliFactory;

    @Override
    public void run() {

    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, cliFactory).execute(args);
    }
}
