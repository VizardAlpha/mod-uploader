package com.github.argon.moduploader.core;

import jakarta.annotation.Nullable;

import java.time.Duration;

public interface Awaitable {
    default void awaits() {
        awaits(null);
    };

    void awaits(@Nullable Duration timeout);
}
