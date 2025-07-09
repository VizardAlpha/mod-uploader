package com.github.argon.moduploader.cli.command;

import com.github.argon.moduploader.cli.command.modio.ModioCommand;
import com.github.argon.moduploader.cli.command.steam.SteamCommand;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {
    SteamCommand.class,
    ModioCommand.class
})
public class EntryCommand implements Runnable, QuarkusApplication {

    @Inject
    CommandLine.IFactory cliFactory;

    @Override
    public void run() {}

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, cliFactory)
            // PicoCLI will only run the last sub command in the chain by default.
            // We want to execute all of them!
            .setExecutionStrategy(new CommandLine.RunAll())
            .execute(args);
    }
}
