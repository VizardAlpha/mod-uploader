package com.github.argon.moduploader.core;

public interface Initializable<T> {
    void init(T init) throws InitializeException;
}
