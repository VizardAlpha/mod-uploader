package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioDtoMapper;
import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioUserDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("me")
@RegisterRestClient
@RegisterProvider(ModioDtoMapper.class) // use custom configured jackson object mapper
public interface ModioUserClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    ModioUserDto getUser(
        @HeaderParam("Authorization") String bearerToken
    );
}
