package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCQuery;

public class SteamSearchQuery extends AbstractSteamQuery {
    private final SteamUGC workshop;
    private final SteamUGC.UGCQueryType queryType;
    private final SteamUGC.MatchingUGCType matchingType;
    private final int creatorAppID;
    private final int consumerAppID;

    public SteamSearchQuery(
        SteamUGC workshop,
        SteamUGC.UGCQueryType queryType,
        SteamUGC.MatchingUGCType matchingType,
        int creatorAppID,
        int consumerAppID
    ) {
        this.workshop = workshop;
        this.queryType = queryType;
        this.matchingType = matchingType;
        this.creatorAppID = creatorAppID;
        this.consumerAppID = consumerAppID;
        next();
    }

    @Override
    public SteamUGCQuery next() {
        page++;
        query = workshop.createQueryAllUGCRequest(
            queryType,
            matchingType,
            creatorAppID,
            consumerAppID,
            page
        );

        return query;
    }
}
