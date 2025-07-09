package com.github.argon.moduploader.core.vendor.steam.api;

import com.codedisaster.steamworks.SteamUGCQuery;
import jakarta.annotation.Nullable;

import java.util.function.Supplier;

public interface SteamQuery extends Supplier<SteamUGCQuery> {
    @Nullable
    SteamUGCQuery get();
    SteamUGCQuery next();
}
