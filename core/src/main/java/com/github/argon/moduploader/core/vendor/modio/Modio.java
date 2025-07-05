package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.VendorException;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Modio {
    private final ModioStoreService storeService;
    private final ModioUserService userService;
    private final ModioAuthService authService;

    public List<ModioMod.Remote> getMods(BearerToken bearerToken) {
        ModioUser modioUser = userService.getUser(bearerToken);
        Long userId = modioUser.id();

        return storeService.getMods(userId);
    }

    public List<ModioMod.Remote> getMods(@Nullable Long userId) {
        return getStoreService().getMods(userId);
    }

    public ModioMod.Remote upload(ModioMod.Local mod, @Nullable String version, @Nullable String changelog) throws VendorException {
        return getStoreService().upload(mod, version, changelog);
    }
}
