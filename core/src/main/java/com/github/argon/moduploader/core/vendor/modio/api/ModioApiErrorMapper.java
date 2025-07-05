package com.github.argon.moduploader.core.vendor.modio.api;

import com.github.argon.moduploader.core.vendor.modio.api.dto.ModioErrorDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class ModioApiErrorMapper implements ResponseExceptionMapper<ModioApiException> {

    @Override
    public ModioApiException toThrowable(Response response) {
        if (!response.hasEntity()) {
            return new ModioApiException(null, response.getStatusInfo().toEnum(), response);
        }

        ModioErrorDto modioErrorDto = response.readEntity(ModioErrorDto.class);
        return new ModioApiException(modioErrorDto, response.getStatusInfo().toEnum(), response);
    }
}
