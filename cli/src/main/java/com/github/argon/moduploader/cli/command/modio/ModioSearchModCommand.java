package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.cli.command.CliPrinter;
import com.github.argon.moduploader.core.vendor.modio.Modio;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "search-mod", description = "Search for mods on mod.io")
public class ModioSearchModCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;
    @Inject CliPrinter cliPrinter;
    @Inject Modio modio;

    @CommandLine.Option(names = {"-n", "--name"},
        description = "Exact name of the mod.")
    String name;

    @CommandLine.Option(names = {"-oid", "--owner-id"},
        description = "Exact id of the owner.")
    Long ownerId;

    @CommandLine.Option(names = {"-o", "--owner"},
        description = "Exact name of the owner of the mod.")
    String ownerName;

    @CommandLine.Option(names = {"-t", "--tags"}, split = ",",
        description = "List of tags the mod must have")
    List<String> tags;

    @Override
    public Integer call() {
        String apiKey = parentCommand.apiKey;
        Long gameId = parentCommand.gameId;

        List<ModioMod.Remote> mods = modio.modService().getMods(apiKey, gameId, ownerId, ownerName, name, tags);
        cliPrinter.printMods(mods);

        return 0;
    }
}
