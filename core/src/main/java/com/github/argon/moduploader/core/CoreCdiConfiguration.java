package com.github.argon.moduploader.core;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.file.IFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class CoreCdiConfiguration {

    @Produces
    @Singleton
    public IFileService getFileService(
        JavaPropsMapper javaPropsMapper
    ) {
        return new FileService(javaPropsMapper);
    }

    @Produces
    @Singleton
    public JavaPropsMapper javaPropsMapper() {
        return JavaPropsMapper.builder().build();
    }
}
