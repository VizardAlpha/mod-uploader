package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.*;

public class SteamCallback implements SteamUGCCallback {

    @Override
    public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults, boolean isCachedData, SteamResult result) {

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

    }

    @Override
    public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {

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
