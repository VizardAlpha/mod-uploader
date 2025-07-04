package com.github.argon.moduploader.core.vendor.modio.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;

/**
 * Custom Jackson Mapper for mapping the mod.io JSON into Java objects
 */
public class ModioDtoMapper implements ContextResolver<ObjectMapper> {
    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        ObjectMapper objectMapper = new ObjectMapper();

        // handle date types like Instant
        objectMapper.registerModule(new JavaTimeModule());
        // our DTOs don't reflect the full JSON
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // skip null values in serialization
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // mod.io uses JSON property names like "date_added"
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        return objectMapper;
    }
}
