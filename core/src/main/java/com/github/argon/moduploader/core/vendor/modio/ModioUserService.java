package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.modio.api.ModioUserClient;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioUserDto;
import com.github.argon.moduploader.core.vendor.modio.model.ModioUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModioUserService {
    private final ModioUserClient userClient;
    private final ModioMapper mapper;

    public ModioUser getUser(BearerToken bearerToken) {
        ModioUserDto user = userClient.getUser(bearerToken.toString());
        return mapper.map(user);
    }
}
