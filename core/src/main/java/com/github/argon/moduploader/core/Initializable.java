package com.github.argon.moduploader.core;

public interface Initializable<T> {
    boolean init(T init) throws InitializeException;
}
