package com.github.argon.moduploader.core.vendor.steam;

import com.codedisaster.steamworks.*;
import com.github.argon.moduploader.core.vendor.steam.model.SteamMod;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * For interacting with the Steam Workshop.
 *
 * {@link Steam#block(Duration)} or {@link Steam#block()} has to be called after method calls.
 * This will start running the SteamAPI callbacks and fetch data from the API.
 */
@Slf4j
public class SteamWorkshopService implements Closeable {

    private static SteamUGC workshop;
    private static SteamUserService user;
    private static Integer appId;

    @Nullable
    private static BiConsumer<SteamPublishedFileID, SteamResult> updateHandler = null;
    @Nullable
    private static BiConsumer<SteamPublishedFileID, SteamResult> creationHandler = null;

    private final static HashMap<SteamUGCQuery, BiConsumer<List<SteamMod.Remote>, SteamResult>> modHandlers = new HashMap<>();

    /**
     * @param appId of the Steam game / application
     */
    public SteamWorkshopService(Integer appId) {
        SteamWorkshopService.user = new SteamUserService();
        SteamWorkshopService.workshop = new SteamUGC(new Callback());
        SteamWorkshopService.appId = appId;
    }

    /**
     * Creates or updates a mod in the Steam Workshop.
     * New mod when {@link SteamMod.Local#publishedFileId()} is null.
     *
     * @param mod to upload
     * @param visibility for hiding the mod for other users
     * @param changelog with changes, you made to the mod
     * @param uploadHandler called when the upload is finished
     */
    public void upload(SteamMod.Local mod, SteamRemoteStorage.PublishedFileVisibility visibility, String changelog, BiConsumer<SteamPublishedFileID, SteamResult> uploadHandler) {
        log.debug("Uploading steam mod {}", mod);

        if (mod.publishedFileId() == null) {
            create((steamPublishedFileID, steamResult) -> {
                update(SteamMapper.map(steamPublishedFileID, mod), visibility, changelog, uploadHandler);
            });
        } else {
            update(mod, visibility, changelog, uploadHandler);
        }
    }

    /**
     * Creates a new Steam Workshop mod remotely
     *
     * @param creationHandler called when creation is done
     */
    public void create(BiConsumer<SteamPublishedFileID, SteamResult> creationHandler) {
        SteamWorkshopService.creationHandler = creationHandler;
        workshop.createItem(appId, SteamRemoteStorage.WorkshopFileType.Community);
    }

    /**
     * Updates a Steam Workshop mod with given {@link SteamMod.Local#publishedFileId()}.
     *
     * @param mod to update
     * @param visibility for hiding the mod for other users
     * @param changelog with changes, you made to the mod
     * @param updateHandler called when the update is done
     */
    public void update(SteamMod.Local mod, SteamRemoteStorage.PublishedFileVisibility visibility, String changelog, BiConsumer<SteamPublishedFileID, SteamResult> updateHandler) {
        if (mod.publishedFileId() == null) {
            log.warn("Cannot update mod without a publishedFileId");
            return;
        }

        SteamWorkshopService.updateHandler = updateHandler;
        SteamUGCUpdateHandle steamUpdateHandle = workshop.startItemUpdate(appId, new SteamPublishedFileID(mod.publishedFileId()));

        workshop.setItemTitle(steamUpdateHandle, mod.title());
        workshop.setItemDescription(steamUpdateHandle, mod.description());
        workshop.setItemTags(steamUpdateHandle, mod.tags().toArray(String[]::new));
        workshop.setItemPreview(steamUpdateHandle, mod.previewImage().toString());
        workshop.setItemContent(steamUpdateHandle, mod.contentFolder().toString());
        workshop.setItemVisibility(steamUpdateHandle, visibility);
        workshop.submitItemUpdate(steamUpdateHandle, changelog);
    }

    public boolean hasHandlers() {
        return !modHandlers.isEmpty() || creationHandler != null || updateHandler != null;
    }

    public void clearHandlers() {
        creationHandler = null;
        updateHandler = null;
        modHandlers.clear();
    }

    @Override
    public void close() {
        clearHandlers();
        user.close();
        workshop.dispose();
    }

    /**
     * Reads all published Steam Workshop mods from the currently logged-in user
     *
     * @param modsHandler called when the search query is finished
     */
    public void fetchPublishedMods(BiConsumer<List<SteamMod.Remote>, SteamResult> modsHandler) {
        SteamUGCQuery queryUserUGCRequest = workshop.createQueryUserUGCRequest(
            user.getSteamID().getAccountID(),
            SteamUGC.UserUGCList.Published,
            SteamUGC.MatchingUGCType.Items,
            SteamUGC.UserUGCListSortOrder.LastUpdatedDesc,
            appId,
            appId,
            modHandlers.size() + 1
        );

        workshop.sendQueryUGCRequest(queryUserUGCRequest);
        modHandlers.put(queryUserUGCRequest, modsHandler);
    }

    /**
     * Here is where the {@link SteamAPI} makes its callbacks
     */
    private static class Callback implements SteamUGCCallback {

        @Override
        public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {
            if (!modHandlers.containsKey(query)) {
                log.debug("Query not handled");
                return;
            }

            if (result != SteamResult.OK) {
                log.warn("Query failed: {}", result);
                modHandlers.get(query).accept(Collections.emptyList(), result);
                modHandlers.remove(query);
                return;
            }

            if (numResultsReturned <= 0) {
                log.info("Nothing found");
                modHandlers.get(query).accept(Collections.emptyList(), result);
                modHandlers.remove(query);
                return;
            }

            ArrayList<SteamUGCDetails> details = new ArrayList<>();
            for (int i = 0; i < numResultsReturned; i++){
                details.add(new SteamUGCDetails());
                workshop.getQueryUGCResult(query, i, details.get(i));
            }

            List<SteamMod.Remote> mods = details.stream()
                .map(SteamMapper::map)
                .toList();

            modHandlers.get(query).accept(mods, result);
            modHandlers.remove(query);
        }

        @Override
        public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {

        }

        @Override
        public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {

        }

        @Override
        public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {

        }

        /**
         * When a new Workshop item was created
         */
        @Override
        public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            if (result != SteamResult.OK) {
                log.warn("Create new workshop item failed: {}", result);
            }

            if (creationHandler == null) {
                log.info("No handler for mod create registered");
                return;
            }

            creationHandler.accept(publishedFileID, result);
            creationHandler = null;
        }

        /**
         * When a Workshop item was updated
         */
        @Override
        public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            if (result != SteamResult.OK) {
                log.warn("Updating workshop item failed: {}", result);
            }

            if (updateHandler == null) {
                log.info("No handler for mod update registered");
                return;
            }

            updateHandler.accept(publishedFileID, result);
            updateHandler = null;
        }

        @Override
        public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {

        }

        @Override
        public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {

        }

        @Override
        public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {

        }

        @Override
        public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {

        }

        @Override
        public void onStartPlaytimeTracking(SteamResult result) {

        }

        @Override
        public void onStopPlaytimeTracking(SteamResult result) {

        }

        @Override
        public void onStopPlaytimeTrackingForAllItems(SteamResult result) {

        }

        @Override
        public void onDeleteItem(SteamPublishedFileID publishedFileID, SteamResult result) {

        }
    }
}
