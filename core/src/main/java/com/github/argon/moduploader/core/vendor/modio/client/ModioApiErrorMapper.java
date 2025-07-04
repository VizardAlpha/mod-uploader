package com.github.argon.moduploader.core.vendor.modio.client;

import com.github.argon.moduploader.core.vendor.modio.client.dto.ModioErrorDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class ModioApiErrorMapper implements ResponseExceptionMapper<ModioApiException> {

    @Override
    public ModioApiException toThrowable(Response response) {
        // FIXME reflection T_T
        ModioErrorDto modioErrorDto = response.readEntity(ModioErrorDto.class);
        return new ModioApiException(modioErrorDto, response);
    }
}
