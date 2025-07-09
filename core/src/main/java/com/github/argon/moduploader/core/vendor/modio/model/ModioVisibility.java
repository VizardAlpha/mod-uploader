package com.github.argon.moduploader.core.vendor.modio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum ModioVisibility {
    VISIBLE(0),
    HIDDEN(1);

    @Getter
    @JsonValue
    private final int value;

    @JsonCreator
    ModioVisibility(int value) {
        this.value = value;
    }
}
