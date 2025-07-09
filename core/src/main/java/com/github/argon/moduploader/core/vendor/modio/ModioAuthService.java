package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.AuthException;
import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.auth.BearerTokenFileConsumer;
import com.github.argon.moduploader.core.auth.BearerTokenFileProvider;
import com.github.argon.moduploader.core.vendor.modio.api.ModioApiException;
import com.github.argon.moduploader.core.vendor.modio.api.ModioOAuthClient;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioAccessTokenDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioEmailRequestResponseDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioLogoutDto;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all authentication related processes for mod.io
 */
@Slf4j
@RequiredArgsConstructor
public class ModioAuthService {
    private final ModioOAuthClient authClient;
    private final BearerTokenFileProvider bearerTokenProvider;
    private final BearerTokenFileConsumer bearerTokenConsumer;

    @Nullable
    public BearerToken getBearerToken() {
        return bearerTokenProvider.get();
    }

    public void setBearerToken(BearerToken bearerToken) {
        bearerTokenConsumer.accept(bearerToken);
    }

    public boolean logout() {
        try {
            ModioLogoutDto logout = authClient.logout(getBearerToken().toString());
            bearerTokenProvider.clear();
            bearerTokenConsumer.clear();
            return logout.success();
        } catch (ModioApiException e) {
            throw new AuthException("Logout failed", e);
        }
    }

    /**
     * Will email the user with a code for logging in
     */
    public void requestEmailCode(String apiKey, String email) {
        try {
            ModioEmailRequestResponseDto emailRequestResponse = authClient.emailRequest(apiKey, email);

            if (emailRequestResponse.code() != Response.Status.OK.getStatusCode()) {
                throw new AuthException("Request email code failed (" + emailRequestResponse.code() + "): " + emailRequestResponse.message());
            }
        } catch (ModioApiException e) {
            throw new AuthException("Request email code failed", e);
        }
    }

    /**
     * Will fetch a "bearer token" to interact with the mod.io in the name of the user.
     * The user counts as logged in when there is a valid bearer token.
     */
    public void exchangeEmailCode(String apiKey, String emailCode) {
        try {
            ModioAccessTokenDto accessToken = authClient.emailExchange(apiKey, emailCode);

            if (accessToken.code() != Response.Status.OK.getStatusCode()) {
                throw new AuthException("Exchange email code failed (" + accessToken.code() + ")");
            }

            bearerTokenConsumer.accept(new BearerToken(accessToken.accessToken(), accessToken.dateExpires()));
        } catch (ModioApiException e) {
            throw new AuthException("Exchange email code failed", e);
        }
    }
}
