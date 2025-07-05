package com.github.argon.moduploader.core.vendor.modio;

import com.github.argon.moduploader.core.auth.AuthException;
import com.github.argon.moduploader.core.auth.BearerToken;
import com.github.argon.moduploader.core.vendor.modio.api.ModioApiException;
import com.github.argon.moduploader.core.vendor.modio.api.ModioOAuthClient;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioAccessTokenDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioEmailRequestResponseDto;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioLogoutDto;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class ModioAuthService {
    private final String apiKey;
    private final ModioOAuthClient authClient;
    private final Provider<BearerToken> bearerTokenProvider;
    private final Consumer<BearerToken> bearerTokenConsumer;

    @Nullable
    public BearerToken getBearerToken() {
        return bearerTokenProvider.get();
    }

    public void setBearerToken(BearerToken bearerToken) {
        bearerTokenConsumer.accept(bearerToken);
    }

    public void logout() {
        try {
            ModioLogoutDto logout = authClient.logout(bearerTokenProvider.get().toString());
            if (logout.success()) {
                throw new AuthException("Logout failed (" + logout.code() + "): " + logout.message());
            }
        } catch (ModioApiException e) {
            throw new AuthException("Logout failed", e);
        }
    }

    public void requestEmailCode(String email) {
        try {
            ModioEmailRequestResponseDto emailRequestResponse = authClient.emailRequest(apiKey, email);

            if (emailRequestResponse.code() != Response.Status.OK.getStatusCode()) {
                throw new AuthException("Request email code failed (" + emailRequestResponse.code() + "): " + emailRequestResponse.message());
            }
        } catch (ModioApiException e) {
            throw new AuthException("Request email code failed", e);
        }
    }

    public void exchangeEmailCode(String emailCode) {
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
