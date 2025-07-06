package com.github.argon.moduploader.core.vendor.modio.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum ModioGameStatus {
    NOT_ACCEPTED(0),
    ACCEPTED(1),
    DELETED(3);

    @Getter
    @JsonValue
    private final int value;

    @JsonCreator
    ModioGameStatus(int value) {
        this.value = value;
    }
}
