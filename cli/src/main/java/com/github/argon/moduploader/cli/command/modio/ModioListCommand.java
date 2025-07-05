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
@CommandLine.Command(name = "list", description = "List your published mods on modio.")
public class ModioListCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Inject
    ModioLoginCommand loginCommand;

    @Inject
    CliPrinter cliPrinter;

    @Override
    public Integer call() {
        if (!parentCommand.init(null)) {
            return 1;
        }

        Modio modio = parentCommand.modio;
        BearerToken bearerToken = modio.getAuthService().getBearerToken();

        // force login
        while (bearerToken == null || bearerToken.isExpired()) {
            new CommandLine(loginCommand).execute( "-e");
            bearerToken = modio.getAuthService().getBearerToken();
        }

        List<ModioMod.Remote> publishedMods = modio.getMods(bearerToken);
        printResult(publishedMods);

        return 0;
    }

    private void printResult(List<ModioMod.Remote> mods) {
        cliPrinter.printTable(mods, mod -> new String[]{
            mod.id().toString(),
            mod.name(),
            mod.owner(),
            mod.ownerId().toString(),
            mod.timeUpdated().toString()
        }, "id", "name", "owner", "ownerId", "timeUpdated");
    }
}
