package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.SteamUGCQuery;
import jakarta.annotation.Nullable;

public abstract class AbstractSteamQuery implements SteamQuery {
    protected int page = 0;
    protected SteamUGCQuery query;

    @Override
    @Nullable
    public SteamUGCQuery get() {
        return query;
    }

    @Override
    public SteamUGCQuery next() {
        return query;
    }
}
