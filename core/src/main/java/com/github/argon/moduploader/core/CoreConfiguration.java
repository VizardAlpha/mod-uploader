package com.github.argon.moduploader.core;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.file.IFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class CoreConfiguration {

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
