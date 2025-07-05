package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.modio.Modio;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "published", description = "List your published mods on modio.")
public class ModioPublishedCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Inject Modio modio;
    @Inject ModioLoginCommand loginCommand;
    @Inject CliPrinter cliPrinter;

    @Override
    public Integer call() {
        BearerToken bearerToken = modio.authService().getBearerToken();

        // force login
        while (bearerToken == null || bearerToken.isExpired()) {
            new CommandLine(loginCommand).execute( "-e");
            bearerToken = modio.authService().getBearerToken();
        }
        String apiKey = parentCommand.apiKey;
        Long gameId = parentCommand.gameId;

        List<ModioMod.Remote> publishedMods = modio.getPublishedMods(apiKey, gameId, bearerToken);
        cliPrinter.printMods(publishedMods);

        return 0;
    }
}
