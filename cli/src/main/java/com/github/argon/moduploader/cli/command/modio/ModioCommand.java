package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.vendor.modio.ModioProperties;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "modio", mixinStandardHelpOptions = true, subcommands = {
    ModioLoginCommand.class,
    ModioPublishedCommand.class,
    ModioUploadCommand.class,
    ModioSearchModCommand.class,
    ModioSearchGameCommand.class,
    ModioDeleteCommand.class,
})
public class ModioCommand {
    @Inject ModioProperties modioProperties;

    String apiKey;
    Long gameId;

    @CommandLine.Option(names = {"-key", "--api-key"},
        description = "The mod.io api access key. You can create a key in your mod.io profile")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @CommandLine.Option(names = {"-gid", "--game-id"},
        description = "Id of the game you want to interact")
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    @PostConstruct
    public void init() {
        if (apiKey == null) {
            apiKey = modioProperties.apiKey().orElse(null);
        }
    }
}
