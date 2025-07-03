package com.github.argon.moduploader.command.steam;

import com.codedisaster.steamworks.SteamRemoteStorage;
import com.github.argon.moduploader.command.CurrentWorkingDir;
import com.github.argon.moduploader.vendor.steam.Steam;
import com.github.argon.moduploader.vendor.steam.SteamMod;
import com.github.argon.moduploader.vendor.steam.SteamModMapper;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@CommandLine.Command(name = "steam-upload", description = "Upload a mod to the Steam Workshop.")
public class SteamUploadCommand implements Runnable {

    @Inject
    CurrentWorkingDir currentWorkingDir;

    @CommandLine.Option(names = {"-app", "--app-id"}, defaultValue = "480")
    Integer appId;

    @CommandLine.Option(names = {"-id", "--published-file-id"})
    Long publishedFileId;

    @CommandLine.Option(names = {"-n", "--name"}, required = true)
    String name;

    @CommandLine.Option(names = {"-desc", "--description"})
    String description;

    @CommandLine.Option(names = {"-cl", "--changelog"})
    String changelog;

    @CommandLine.Option(names = {"-vis", "--visibility"})
    SteamRemoteStorage.PublishedFileVisibility visibility;

    @CommandLine.Option(names = {"-cf", "--content-folder"}, required = true)
    Path contentFolder;

    @CommandLine.Option(names = {"-img", "--image"}, required = true)
    Path previewImage;

    @CommandLine.Option(names = {"-t", "--tags"})
    List<String> tags = Collections.emptyList();

    @Override
    public void run() {
        try {
            Path currentWorkingDirPath = currentWorkingDir.getPath();

            try (Steam steam = new Steam(appId, currentWorkingDirPath)) {
                steam.getWorkshop().upload(
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
                    System.out.println(SteamModMapper.map(steamPublishedFileID));
                }).block();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
