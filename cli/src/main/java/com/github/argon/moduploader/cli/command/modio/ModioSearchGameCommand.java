package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.modio.Modio;
import com.github.argon.moduploader.core.vendor.modio.model.ModioGame;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "search-game", description = "Search for games on mod.io")
public class ModioSearchGameCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Inject Modio modio;
    @Inject CliPrinter cliPrinter;

    @CommandLine.Option(names = {"-n", "--name"},
        description = "Exact name of the mod.")
    String name;

    @CommandLine.Option(names = {"-oid", "--owner-id"},
        description = "Exact id of the owner.")
    Long ownerId;

    @Override
    public Integer call() {
        String apiKey = parentCommand.apiKey;
        Long gameId = parentCommand.gameId;

        List<ModioGame> games = modio.gameService().getGames(apiKey, gameId, ownerId, name);
        cliPrinter.printModioGames(games);

        return 0;
    }
}
