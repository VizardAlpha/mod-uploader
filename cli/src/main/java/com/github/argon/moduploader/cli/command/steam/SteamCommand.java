package com.github.argon.moduploader.cli.command.steam;

import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.SteamConfiguration;
import com.github.argon.moduploader.core.vendor.steam.SteamProperties;
import io.quarkus.arc.Unremovable;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import picocli.CommandLine;

@Unremovable
@ApplicationScoped
@CommandLine.Command(name = "steam", mixinStandardHelpOptions = true, subcommands = {
    SteamUploadCommand.class,
    SteamPublishedCommand.class,
    SteamSearchGameCommand.class,
    SteamSearchModCommand.class,
    SteamDeleteCommand.class
})
public class SteamCommand implements Runnable {
    @Inject SteamProperties steamProperties;
    @Inject Steam steam;

    Integer appId;
    String apiKey;

    @CommandLine.Option(names = {"-key", "--api-key"},
        description = "The mod.io api access key.")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = SteamConfiguration.DEFAULT_APP_ID_STRING,
        description = "The Steam app id of the game you want to upload a mod to.")
    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    @PostConstruct
    public void postConstruct() {
        if (apiKey == null) {
            apiKey = steamProperties.apiKey().orElse(null);
        }
    }

    @Override
    public void run() {
        steam.init(appId);
    }
}
