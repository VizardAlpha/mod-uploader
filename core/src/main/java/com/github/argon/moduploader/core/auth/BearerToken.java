package com.github.argon.moduploader.core.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class BearerToken {
    public final static String BEARER_TOKEN = "Bearer ";
    private final String tokenString;
    private final Instant dateExpires;

    @Override
    public String toString() {
        return BEARER_TOKEN + tokenString;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(dateExpires);
    }
}
