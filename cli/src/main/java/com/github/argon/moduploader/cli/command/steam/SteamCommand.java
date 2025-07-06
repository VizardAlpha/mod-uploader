package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.core.vendor.steam.SteamProperties;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import picocli.CommandLine;

@ApplicationScoped
@CommandLine.Command(name = "steam", mixinStandardHelpOptions = true, subcommands = {
    SteamUploadCommand.class,
    SteamPublishedCommand.class,
    SteamSearchGameCommand.class,
    SteamSearchModCommand.class,
    SteamDeleteCommand.class
})
public class SteamCommand {
    @Inject SteamProperties steamProperties;

    Integer appId;
    String apiKey;

    @CommandLine.Option(names = {"-key", "--api-key"},
        description = "The mod.io api access key.")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480",
        description = "The Steam app id of the game you want to upload a mod to.")
    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    @PostConstruct
    public void init() {
        if (apiKey == null) {
            apiKey = steamProperties.apiKey().orElse(null);
        }
    }
}
