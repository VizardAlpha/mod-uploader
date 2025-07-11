package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.modio.Modio;
import com.github.argon.moduploader.core.vendor.modio.model.ModioCommunityOptions;
import com.github.argon.moduploader.core.vendor.modio.model.ModioCreditOptions;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMaturityOptions;
import com.github.argon.moduploader.core.vendor.modio.model.ModioVisibility;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "upload", description = "Upload a mod to mod.io")
public class ModioUploadCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    @Inject Modio modio;
    @Inject ModioLoginCommand loginCommand;

    @CommandLine.Option(names = {"-id", "--mod-id"},
        description = "Identifies the mod in mod.io. Will create a new mod when empty or update a mod with the given id.")
    Long modId;

    @CommandLine.Option(names = {"-n", "--name"}, required = true,
        description = "Title of your mod.")
    String name;

    @CommandLine.Option(names = {"-desc", "--description"},
        description = "Your mod description.")
    String description;

    @CommandLine.Option(names = {"-sum", "--summary"}, required = true,
        description = "A short description.")
    String summary;

    @CommandLine.Option(names = {"-ver", "--version"},
        description = "Version of the file release (recommended format 1.0.0 - MAJOR.MINOR.PATCH).")
    String version;

    @CommandLine.Option(names = {"-cl", "--changelog"},
        description = "When updating a mod, you can leave patch notes.")
    String changelog;

    @CommandLine.Option(names = {"-vis", "--visibility"},
        description = "Influences who can see your mod")
    ModioVisibility visibility;

    @CommandLine.Option(names = {"-cf", "--content-folder"}, required = true,
        description = "The folder with your mod files to upload.")
    Path contentFolder;

    @CommandLine.Option(names = {"-i", "--image"}, required = true,
        description = "The thumbnail and preview image of the mod.")
    Path logo;

    @CommandLine.Option(names = {"-t", "--tags"}, split = ",",
        description = "A list of tags to help users finding your mod.")
    List<String> tags = List.of();

    @Override
    public Integer call() {
        BearerToken bearerToken = modio.authService().getBearerToken();

        // force login
        while (bearerToken == null || bearerToken.isExpired()) {
            new CommandLine(loginCommand).execute( "-e");
            bearerToken = modio.authService().getBearerToken();
        }

        ModioMod.Local mod = new ModioMod.Local(
            modId,
            name,
            null,
            summary,
            description,
            logo,
            contentFolder,
            null,
            visibility,
            ModioMaturityOptions.NONE,
            ModioCreditOptions.NONE,
            ModioCommunityOptions.ALL,
            null,
            null,
            tags
        );

        try {
            Long gameId = parentCommand.gameId;
            ModioMod.Remote remote = modio.upload(gameId, mod, version, changelog);
            System.out.println(remote.id());
        } catch (VendorException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }
}
