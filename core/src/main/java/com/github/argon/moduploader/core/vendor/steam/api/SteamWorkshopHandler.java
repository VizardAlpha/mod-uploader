package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.*;
import com.github.argon.moduploader.core.vendor.VendorException;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
public class SteamWorkshopHandler extends SteamCallback implements Closeable {
    private final Integer appId;
    private final SteamUGC workshop;

    @Nullable
    private BiConsumer<SteamPublishedFileID, SteamResult> updateHandler = null;
    @Nullable
    private BiConsumer<SteamPublishedFileID, SteamResult> creationHandler = null;
    @Nullable
    private BiConsumer<SteamPublishedFileID, SteamResult> deleteHandler = null;
    private final Map<SteamQuery, SteamResults> queryHandlers = new HashMap<>();
    private final Map<SteamUGCQuery, SteamQuery> queryToQuery = new HashMap<>();

    public SteamWorkshopHandler(Integer appId) {
        this.workshop = new SteamUGC(this);
        this.appId = appId;
    }

    public void upload(
        @Nullable SteamPublishedFileID modId,
        String name,
        String description,
        List<String> tags,
        Path previewImage,
        Path contentFolder,
        SteamRemoteStorage.PublishedFileVisibility visibility,
        String changelog, BiConsumer<SteamPublishedFileID, SteamResult> uploadHandler
    ) {
        log.debug("Uploading steam mod");
        if (modId == null) {
            create((createdModId, steamResult) -> {
                update(createdModId, name, description, tags, previewImage, contentFolder, visibility, changelog, uploadHandler);
            });
        } else {
            update(modId, name, description, tags, previewImage, contentFolder, visibility, changelog, uploadHandler);
        }
    }

    public void create(BiConsumer<SteamPublishedFileID, SteamResult> creationHandler) {
        this.creationHandler = creationHandler;
        workshop.createItem(appId, SteamRemoteStorage.WorkshopFileType.Community);
    }

    public void update(
        @NonNull SteamPublishedFileID modId,
        String name,
        String description,
        List<String> tags,
        Path previewImage,
        Path contentFolder,
        SteamRemoteStorage.PublishedFileVisibility visibility,
        String changelog,
        BiConsumer<SteamPublishedFileID, SteamResult> updateHandler
    ) {
        this.updateHandler = updateHandler;
        SteamUGCUpdateHandle steamUpdateHandle = workshop.startItemUpdate(appId, modId);

        workshop.setItemTitle(steamUpdateHandle, name);
        workshop.setItemDescription(steamUpdateHandle, description);
        workshop.setItemTags(steamUpdateHandle, tags.toArray(String[]::new));
        workshop.setItemPreview(steamUpdateHandle, previewImage.toString());
        workshop.setItemContent(steamUpdateHandle, contentFolder.toString());
        workshop.setItemVisibility(steamUpdateHandle, visibility);

        workshop.submitItemUpdate(steamUpdateHandle, changelog);
    }

    public void deleteMod(SteamPublishedFileID modId, BiConsumer<SteamPublishedFileID, SteamResult> deleteHandler) {
        this.deleteHandler = deleteHandler;
        workshop.deleteItem(modId);
    }

    public void getMod(SteamPublishedFileID modId, BiConsumer<List<SteamUGCDetails>, SteamResult> modsHandler) {
        SteamSingleQuery steamSingleQuery = new SteamSingleQuery(workshop, modId);
        send(steamSingleQuery, modsHandler);
    }

    public void getMods(BiConsumer<List<SteamUGCDetails>, SteamResult> modsHandler) {
        SteamSearchQuery steamSearchQuery = new SteamSearchQuery(
            workshop,
            SteamUGC.UGCQueryType.RankedByPublicationDate,
            SteamUGC.MatchingUGCType.Items,
            appId,
            appId
        );

        send(steamSearchQuery, modsHandler);
    }

    public void searchMods(String searchText, BiConsumer<List<SteamUGCDetails>, SteamResult> modsHandler) throws VendorException {
        SteamSearchQuery steamSearchQuery = new SteamSearchQuery(
            workshop,
            SteamUGC.UGCQueryType.RankedByTextSearch,
            SteamUGC.MatchingUGCType.Items,
            appId,
            appId
        );

        if(!workshop.setSearchText(steamSearchQuery.get(), searchText)) {
            throw new VendorException("Invalid SteamUGCQuery");
        }

        send(steamSearchQuery, modsHandler);
    }

    public void getPublishedMods(Integer accountID, BiConsumer<List<SteamUGCDetails>, SteamResult> modsHandler) {
        SteamUserQuery steamUserQuery = new SteamUserQuery(
            workshop,
            accountID,
            SteamUGC.UserUGCList.Published,
            SteamUGC.MatchingUGCType.Items,
            SteamUGC.UserUGCListSortOrder.LastUpdatedDesc,
            appId,
            appId
        );

        send(steamUserQuery, modsHandler);
    }

    public void send(SteamQuery query, BiConsumer<List<SteamUGCDetails>, SteamResult> callback) {
        SteamUGCQuery steamUGCQuery = query.get();

        // is this a new request?
        if (!queryHandlers.containsKey(query)) {
            queryHandlers.put(query, new SteamResults(query, callback));
        }

        // is this query already linked to a request?
        if (!queryToQuery.containsKey(steamUGCQuery)) {
            queryToQuery.put(steamUGCQuery, query);
        }

        workshop.sendQueryUGCRequest(steamUGCQuery);
    }

    public boolean hasHandlers() {
        return !queryHandlers.isEmpty() || creationHandler != null || updateHandler != null || deleteHandler != null;
    }

    public void clearHandlers() {
        creationHandler = null;
        updateHandler = null;
        deleteHandler = null;
        queryHandlers.clear();
    }

    @Override
    public void close() {
        clearHandlers();
        workshop.dispose();
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

    @Override
    public void onUGCQueryCompleted(SteamUGCQuery steamUGCQuery, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {
        if (!queryToQuery.containsKey(steamUGCQuery)) {
            log.warn("Query {} not handled", steamUGCQuery);
            return;
        }

        SteamQuery steamQuery = queryToQuery.remove(steamUGCQuery);
        SteamResults steamResults = queryHandlers.get(steamQuery);

        if (result != SteamResult.OK) {
            log.warn("Query failed: {}", result);
            steamResults.getCallback().accept(List.of(), result);
            queryHandlers.remove(steamQuery);
            return;
        }

        if (totalMatchingResults == 0) {
            log.debug("Nothing found");
            steamResults.getCallback().accept(List.of(), result);
            queryHandlers.remove(steamQuery);
            return;
        }

        int leftover = totalMatchingResults - steamResults.getResults().size();
        int resultCount = Math.min(leftover, numResultsReturned);

        // gather steam mod results
        ArrayList<SteamUGCDetails> details = new ArrayList<>();
        for (int i = 0; i < resultCount; i++){
            details.add(new SteamUGCDetails());
            workshop.getQueryUGCResult(steamUGCQuery, i, details.get(i));
        }
        steamResults.getResults().addAll(details);

        // are we finished?
        if (steamResults.getResults().size() >= totalMatchingResults) {
            queryHandlers.remove(steamQuery);
            steamResults.getCallback().accept(steamResults.getResults(), result);
            return;
        }

        // generate a query for the next page
        steamResults.getQuery().next();
        send(steamResults.getQuery(), steamResults.getCallback());
    }

    @Override
    public void onDeleteItem(SteamPublishedFileID publishedFileID, SteamResult result) {
        if (result != SteamResult.OK) {
            log.warn("Delete workshop item failed: {}", result);
        }

        if (deleteHandler == null) {
            log.info("No handler for mod delete registered");
            return;
        }

        deleteHandler.accept(publishedFileID, result);
        deleteHandler = null;
    }

    @Getter
    @RequiredArgsConstructor
    private static class SteamResults {
        private final SteamQuery query;
        private final BiConsumer<List<SteamUGCDetails>, SteamResult> callback;
        private final List<SteamUGCDetails> results = new ArrayList<>();
    }
}
