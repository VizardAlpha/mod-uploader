package com.github.argon.moduploader.core.vendor.modio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum ModioStatus {
    NOT_ACCEPTED(0),
    ACCEPTED(1),
    DELETED(3);

    @Getter
    @JsonValue
    private final int value;

    @JsonCreator
    ModioStatus(int value) {
        this.value = value;
    }
}
