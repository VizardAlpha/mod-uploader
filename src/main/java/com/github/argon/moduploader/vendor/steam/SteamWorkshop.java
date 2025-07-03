package com.github.argon.moduploader.vendor.steam;

import com.codedisaster.steamworks.*;
import com.github.argon.moduploader.vendor.Blockable;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * For interacting with the Steam Workshop.
 *
 * {@link SteamWorkshop#block(Duration)} or {@link SteamWorkshop#block()} has to be called after method calls.
 * This will start running the SteamAPI callbacks and fetch data from the API.
 */
public class SteamWorkshop implements Closeable, Blockable {

    private final static Logger log = LoggerFactory.getLogger(SteamWorkshop.class);

    private static SteamUGC workshop;
    private static SteamUser user;
    private static Integer appId;

    @Nullable
    private static BiConsumer<SteamPublishedFileID, SteamResult> updateConsumer = null;
    @Nullable
    private static BiConsumer<SteamPublishedFileID, SteamResult> creationConsumer = null;

    private final static HashMap<SteamUGCQuery, BiConsumer<List<SteamMod.Remote>, SteamResult>> modConsumers = new HashMap<>();

    /**
     * @param appId of the Steam game / application
     */
    public SteamWorkshop(Integer appId) {
        SteamWorkshop.user = new SteamUser();
        SteamWorkshop.workshop = new SteamUGC(new Callback());
        SteamWorkshop.appId = appId;
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
    public SteamWorkshop upload(SteamMod.Local mod, SteamRemoteStorage.PublishedFileVisibility visibility, String changelog, BiConsumer<SteamPublishedFileID, SteamResult> uploadHandler) {
        log.debug("Uploading steam mod {}", mod);

        if (mod.publishedFileId() == null) {
            return create((steamPublishedFileID, steamResult) -> {
                update(SteamModMapper.map(steamPublishedFileID, mod), visibility, changelog, uploadHandler);
            });
        } else {
            return update(mod, visibility, changelog, uploadHandler);
        }
    }

    /**
     * Creates a new Steam Workshop mod remotely
     *
     * @param creationHandler called when creation is done
     */
    public SteamWorkshop create(BiConsumer<SteamPublishedFileID, SteamResult> creationHandler) {
        creationConsumer = creationHandler;
        workshop.createItem(appId, SteamRemoteStorage.WorkshopFileType.Community);

        return this;
    }

    /**
     * Updates a Steam Workshop mod with given {@link SteamMod.Local#publishedFileId()}.
     *
     * @param mod to update
     * @param visibility for hiding the mod for other users
     * @param changelog with changes, you made to the mod
     * @param updateHandler called when the update is done
     * @return
     */
    public SteamWorkshop update(SteamMod.Local mod, SteamRemoteStorage.PublishedFileVisibility visibility, String changelog, BiConsumer<SteamPublishedFileID, SteamResult> updateHandler) {
        if (mod.publishedFileId() == null) {
            log.warn("Cannot update mod without a publishedFileId");
            return this;
        }

        updateConsumer = updateHandler;
        SteamUGCUpdateHandle steamUpdateHandle = workshop.startItemUpdate(appId, new SteamPublishedFileID(mod.publishedFileId()));

        workshop.setItemTitle(steamUpdateHandle, mod.title());
        workshop.setItemDescription(steamUpdateHandle, mod.description());
        workshop.setItemTags(steamUpdateHandle, mod.tags().toArray(String[]::new));
        workshop.setItemPreview(steamUpdateHandle, mod.previewImage().toString());
        workshop.setItemContent(steamUpdateHandle, mod.contentFolder().toString());
        workshop.setItemVisibility(steamUpdateHandle, visibility);
        workshop.submitItemUpdate(steamUpdateHandle, changelog);

        return this;
    }

    @Override
    public void close() {
        user.close();
        workshop.dispose();
    }

    /**
     * Reads all published Steam Workshop mods from the currently logged-in user
     *
     * @param modsHandler called when the search query is finished
     */
    public SteamWorkshop fetchPublishedMods(BiConsumer<List<SteamMod.Remote>, SteamResult> modsHandler) {
        SteamUGCQuery queryUserUGCRequest = workshop.createQueryUserUGCRequest(
            user.getSteamID().getAccountID(),
            SteamUGC.UserUGCList.Published,
            SteamUGC.MatchingUGCType.Items,
            SteamUGC.UserUGCListSortOrder.LastUpdatedDesc,
            appId,
            appId,
            modConsumers.size() + 1
        );

        workshop.sendQueryUGCRequest(queryUserUGCRequest);
        modConsumers.put(queryUserUGCRequest, modsHandler);

        return this;
    }

    /**
     * Executes {@link SteamAPI#runCallbacks()} in a loop.
     * This is crucial. Without this, SteamAPI will not do anything.
     *
     * @param timeout optional for ending the loop after a certain duration
     */
    @Override
    public void block(@Nullable Duration timeout) {
        Instant end = null;
        if (timeout != null) {
            end = Instant.now().plus(timeout);
        }

        while ((!modConsumers.isEmpty() || creationConsumer != null || updateConsumer != null) && SteamAPI.isSteamRunning()) {
            SteamAPI.runCallbacks();

            try {
                Thread.sleep(1000 / 15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // no timeout?
            if (end == null) {
                continue;
            }

            if (Instant.now().isAfter(end)) {
                return;
            }
        }
    }

    private static class Callback implements SteamUGCCallback {

        @Override
        public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {
            if (!modConsumers.containsKey(query)) {
                log.debug("Query not handled");
                return;
            }

            if (result != SteamResult.OK) {
                log.warn("Query failed: {}", result);
                modConsumers.get(query).accept(Collections.emptyList(), result);
                modConsumers.remove(query);
                return;
            }

            if (numResultsReturned <= 0) {
                log.info("Nothing found");
                modConsumers.get(query).accept(Collections.emptyList(), result);
                modConsumers.remove(query);
                return;
            }

            ArrayList<SteamUGCDetails> details = new ArrayList<>();
            for (int i = 0; i < numResultsReturned; i++){
                details.add(new SteamUGCDetails());
                workshop.getQueryUGCResult(query, i, details.get(i));
            }

            List<SteamMod.Remote> mods = details.stream()
                .map(SteamModMapper::map)
                .toList();

            modConsumers.get(query).accept(mods, result);
            modConsumers.remove(query);
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

        @Override
        public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            if (result != SteamResult.OK) {
                log.warn("Create new workshop item failed: {}", result);
            }

            if (creationConsumer == null) {
                log.info("No handler for mod create registered");
                return;
            }

            creationConsumer.accept(publishedFileID, result);
            creationConsumer = null;
        }

        @Override
        public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            if (result != SteamResult.OK) {
                log.warn("Updating workshop item failed: {}", result);
            }

            if (updateConsumer == null) {
                log.info("No handler for mod update registered");
                return;
            }

            updateConsumer.accept(publishedFileID, result);
            updateConsumer = null;
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
