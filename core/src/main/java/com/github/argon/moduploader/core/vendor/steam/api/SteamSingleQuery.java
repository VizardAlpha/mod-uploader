package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCQuery;

public class SteamSingleQuery extends AbstractSteamQuery {
    private final SteamUGCQuery query;

    public SteamSingleQuery(SteamUGC workshop, SteamPublishedFileID modId) {
        this.query = workshop.createQueryUGCDetailsRequest(modId);
    }

}
