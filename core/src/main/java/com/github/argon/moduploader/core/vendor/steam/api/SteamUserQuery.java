package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCQuery;

public class SteamUserQuery extends AbstractSteamQuery {
    private final SteamUGC workshop;
    private final int accountID;
    private final SteamUGC.UserUGCList listType;
    private final SteamUGC.MatchingUGCType matchingType;
    private final SteamUGC.UserUGCListSortOrder sortOrder;
    private final int creatorAppID; 
    private final int consumerAppID; 

    public SteamUserQuery(
        SteamUGC workshop, 
        int accountID,
        SteamUGC.UserUGCList listType,
        SteamUGC.MatchingUGCType matchingType,
        SteamUGC.UserUGCListSortOrder sortOrder,
        int creatorAppID,
        int consumerAppID
    ) {
        this.workshop = workshop;
        this.accountID = accountID;
        this.listType = listType;
        this.matchingType = matchingType;
        this.sortOrder = sortOrder;
        this.creatorAppID = creatorAppID;
        this.consumerAppID = consumerAppID;
        next();
    }

    @Override
    public SteamUGCQuery next() {
        page++;
        query = workshop.createQueryUserUGCRequest(
            accountID,
            listType,
            matchingType, 
            sortOrder,
            creatorAppID, 
            consumerAppID,
            page
        );

        return query;
    }
}
