package com.github.argon.moduploader.vendor.steam;

import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamUGCDetails;

import java.time.Instant;
import java.util.Arrays;

public class SteamModMapper {
    public static SteamMod.Remote map(SteamUGCDetails details) {
        return new SteamMod.Remote(
            map(details.getPublishedFileID()),
            details.getTitle(),
            Arrays.stream(details.getTags().split(",")).toList(),
            details.getDescription(),
            details.getFileSize(),
            details.getOwnerID().getAccountID(),
            Instant.ofEpochSecond(details.getTimeCreated()),
            Instant.ofEpochSecond(details.getTimeUpdated()),
            details.getVotesUp(),
            details.getVotesDown()
        );
    }

    public static SteamMod.Local map(SteamPublishedFileID publishedFileID, SteamMod.Local mod) {
        return new SteamMod.Local(
            map(publishedFileID),
            mod.title(),
            mod.description(),
            mod.tags(),
            mod.contentFolder(),
            mod.previewImage()
        );
    }

    public static Long map(SteamPublishedFileID publishedFileID) {
        return Long.parseLong(publishedFileID.toString(), 16);
    }
}
