package com.github.argon.moduploader.cli.command.steam;

import com.codedisaster.steamworks.SteamRemoteStorage;
import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.vendor.steam.Steam;
import com.github.argon.moduploader.core.vendor.steam.model.SteamMod;
import com.github.argon.moduploader.core.vendor.steam.SteamMapper;
import com.github.argon.moduploader.core.vendor.steam.SteamWorkshopService;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@CommandLine.Command(name = "upload", description = "Upload a mod to the Steam Workshop.")
public class SteamUploadCommand implements Runnable {

    @Inject
    FileService fileService;

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480",
        description = "The Steam app id of the game you want to upload a mod to.")
    Integer appId;

    @CommandLine.Option(names = {"-id", "--published-file-id"},
        description = "Identifies the mod in the Steam Workshop. Will create a new mod when empty or update a mod with the given id.")
    Long publishedFileId;

    @CommandLine.Option(names = {"-n", "--name"}, required = true,
        description = "Title of your mod.")
    String name;

    @CommandLine.Option(names = {"-desc", "--description"},
        description = "Your mod description.")
    String description;

    @CommandLine.Option(names = {"-cl", "--changelog"},
        description = "When updating a mod, you can leave patch notes.")
    String changelog;

    @CommandLine.Option(names = {"-vis", "--visibility"},
        description = "Influences who can see your mod")
    SteamRemoteStorage.PublishedFileVisibility visibility;

    @CommandLine.Option(names = {"-cf", "--content-folder"}, required = true,
        description = "The folder with your mod files to upload.")
    Path contentFolder;

    @CommandLine.Option(names = {"-img", "--image"}, required = true,
        description = "The thumbnail and preview image of the mod.")
    Path previewImage;

    @CommandLine.Option(names = {"-t", "--tags"},
        description = "A list of tags to help users finding your mod.")
    List<String> tags = Collections.emptyList();

    @Override
    public void run() {
        try {
            try (Steam steam = new Steam(appId, fileService)) {
                try (SteamWorkshopService workshop = steam.getWorkshop()) {
                    workshop.upload(
                        new SteamMod.Local(
                            publishedFileId,
                            name,
                            description,
                            tags,
                            contentFolder.toAbsolutePath(),
                            previewImage.toAbsolutePath()
                        ),
                        visibility,
                        changelog,
                        (steamPublishedFileID, steamResult) -> {
                            System.out.println(steamResult.toString());
                            System.out.println(SteamMapper.map(steamPublishedFileID));
                        });
                }

                steam.block();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
