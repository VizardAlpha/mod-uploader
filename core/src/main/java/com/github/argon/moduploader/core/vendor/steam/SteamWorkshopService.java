package com.github.argon.moduploader.core.vendor.steam;

import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamRemoteStorage;
import com.codedisaster.steamworks.SteamResult;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.steam.api.SteamUserHandler;
import com.github.argon.moduploader.core.vendor.steam.api.SteamWorkshopHandler;
import com.github.argon.moduploader.core.vendor.steam.model.SteamMod;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * For interacting with the Steam Workshop.
 *
 * {@link Steam#awaits(Duration)} or {@link Steam#awaits()} has to be called after method calls.
 * This will start running the SteamAPI callbacks and fetch data from the API.
 */
@Slf4j
@RequiredArgsConstructor
public class SteamWorkshopService implements Closeable {
    private final SteamWorkshopHandler workshop;
    private final SteamMapper mapper;
    private final SteamUserHandler user;

    /**
     * Creates or updates a mod in the Steam Workshop.
     * New mod when {@link SteamMod.Local#id()} is null.
     *
     * @param mod to upload
     * @param visibility for hiding the mod for other users
     * @param changelog with changes, you made to the mod
     * @param uploadHandler called when the upload is finished
     */
    public void upload(SteamMod.Local mod, SteamRemoteStorage.PublishedFileVisibility visibility, String changelog, BiConsumer<Long, SteamResult> uploadHandler) {
        log.debug("Uploading steam mod {}", mod);

        SteamPublishedFileID modId = null;

        if (mod.id() != null) {
            modId = new SteamPublishedFileID(mod.id());
        }

        workshop.upload(
            modId,
            mod.name(),
            mod.description(),
            mod.tags(),
            mod.previewImage(),
            mod.contentFolder(),
            visibility,
            changelog,
            (steamPublishedFileID, result) -> {
                uploadHandler.accept(mapper.toLong(steamPublishedFileID), result);
            }
        );
    }

    /**
     * Creates a new Steam Workshop mod remotely
     *
     * @param creationHandler called when creation is done
     */
    public void create(BiConsumer<Long, SteamResult> creationHandler) {
        workshop.create((steamPublishedFileID, result) -> {
            creationHandler.accept(mapper.toLong(steamPublishedFileID), result);
        });
    }

    /**
     * Updates a Steam Workshop mod with given {@link SteamMod.Local#id()}.
     *
     * @param mod to update
     * @param visibility for hiding the mod for other users
     * @param changelog with changes, you made to the mod
     * @param updateHandler called when the update is done
     */
    public void update(SteamMod.Local mod, SteamRemoteStorage.PublishedFileVisibility visibility, String changelog, BiConsumer<Long, SteamResult> updateHandler) {
        assert mod.id() != null;
        SteamPublishedFileID modId = new SteamPublishedFileID(mod.id());

        workshop.update(
            modId,
            mod.name(),
            mod.description(),
            mod.tags(),
            mod.previewImage(),
            mod.contentFolder(),
            visibility,
            changelog,
            (steamPublishedFileID, result) -> {
                updateHandler.accept(mapper.toLong(steamPublishedFileID), result);
            }
        );
    }

    public void getMod(@NonNull Long modId, BiConsumer<SteamMod.Remote, SteamResult> modsHandler) {
        workshop.getMod(new SteamPublishedFileID(modId), (steamUGCDetailsList, result) -> {
            if (steamUGCDetailsList.isEmpty()) {
                log.debug("No steam mod found for id {}", modId);
                modsHandler.accept(null, result);
            }
            SteamMod.Remote mod = mapper.map(steamUGCDetailsList.getFirst());
            modsHandler.accept(mod, result);
        });
    }

    public void getMods(BiConsumer<List<SteamMod.Remote>, SteamResult> modsHandler) {
        workshop.getMods((steamUGCDetailsList, result) -> {
            List<SteamMod.Remote> mods = steamUGCDetailsList.stream()
                .map(mapper::map)
                .toList();

            modsHandler.accept(mods, result);
        });
    }

    public void searchMods(String searchText, BiConsumer<List<SteamMod.Remote>, SteamResult> modsHandler) throws VendorException {
        workshop.searchMods(searchText, (steamUGCDetailsList, result) -> {
            List<SteamMod.Remote> mods = steamUGCDetailsList.stream()
                .map(mapper::map)
                .toList();

            modsHandler.accept(mods, result);
        });
    }

    /**
     * Reads all published Steam Workshop mods from the currently logged-in user
     *
     * @param modsHandler called when the search query is finished
     */
    public void getPublishedMods(BiConsumer<List<SteamMod.Remote>, SteamResult> modsHandler) {
       workshop.getPublishedMods(user.getSteamID().getAccountID(), (steamUGCDetailsList, result) -> {
           List<SteamMod.Remote> mods = steamUGCDetailsList.stream()
               .map(mapper::map)
               .toList();

           modsHandler.accept(mods, result);
       });
    }

    public boolean hasHandlers() {
        return workshop.hasHandlers();
    }

    @Override
    public void close() {
        workshop.close();
        user.close();
    }
}
