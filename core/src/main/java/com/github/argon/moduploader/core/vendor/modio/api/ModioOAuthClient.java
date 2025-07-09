package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * For communicating with the mod.io "oauth" api via REST client.
 */
@Path("oauth")
@RegisterRestClient
@RegisterProvider(ModioDtoMapper.class) // use custom configured jackson object mapper
public interface ModioOAuthClient {

    /**
     * Invalidates the bearer token and logs the user out
     *
     * @param bearerToken to invalidate
     */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ModioLogoutDto logout(
        @HeaderParam("Authorization") String bearerToken
    );

    /**
     * Request a security code for a user, identified by their e-mail which can then be exchanged for an access token.
     */
    @POST
    @Path("/emailrequest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    ModioEmailRequestResponseDto emailRequest(
        @QueryParam("api_key") String apiKey,
        @FormParam("email") String email
    );

    /**
     * Exchange a security code issued from the /oauth/emailrequest endpoint for an access token.
     * To use this functionality, you must use your games api_key from your games profile
     * on mod.io and the same api_key must be used from the original request for a security code.
     */
    @POST
    @Path("/emailexchange")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    ModioAccessTokenDto emailExchange(
        @QueryParam("api_key") String apiKey,
        @FormParam("security_code") String code
    );
}
