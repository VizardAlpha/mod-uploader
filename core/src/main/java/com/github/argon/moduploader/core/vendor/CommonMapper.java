package com.github.argon.moduploader.core.vendor;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Mapper
public interface CommonMapper {
    @Named("toList")
    default List<String> toList(String commasListString) {
        return Arrays.asList(commasListString.split(","));
    }

    @Named("toInstant")
    default Instant toInstant(Integer seconds) {
        return Instant.ofEpochSecond(seconds);
    }
}
