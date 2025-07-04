package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.modio.client.ModioUserClient;
import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioUserDto;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModioUserService {
    private final ModioUserClient modioUserClient;

    public ModioUser getUser(BearerToken bearerToken) {
        ModioUserDto user = modioUserClient.getUser(bearerToken.toString());
        return ModioMapper.map(user);
    }
}
