package com.github.argon.moduploader.core.file;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;

/**
 * Simple service for handling file operations.
 */
@Slf4j
@RequiredArgsConstructor
public class FileService extends AbstractFileService {
    public final static Charset CHARSET = StandardCharsets.UTF_8;
    public final static Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"));
    private final JavaPropsMapper javaPropsMapper;

    @Override
    public <T> Path writeAsProperties(Path path, T pojo) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        Properties properties = javaPropsMapper.writeValueAsProperties(pojo);
        OutputStream outputStream = Files.newOutputStream(absolutePath);

        properties.store(outputStream, pojo.getClass().getName());
        return absolutePath;
    }

    @Override
    public Path zip(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        Path tempFile = Files.createTempFile(TEMP_DIR, path.getFileName().toString(), ".zip");

        try (ZipFile zipFile = new ZipFile(tempFile.toFile())) {
            if (Files.isDirectory(path)) {
                zipFile.addFolder(absolutePath.toFile());
            } else {
                zipFile.addFile(absolutePath.toFile());
            }
        }

        return tempFile;
    }

    public List<String> readLines(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Reading lines from file {}", absolutePath);

        if (!Files.exists(absolutePath)) {
            // do not load what's not there
            log.info("{} is not a folder, does not exists or is not readable", absolutePath);
            return List.of();
        }

        try (InputStream inputStream = Files.newInputStream(absolutePath)) {
            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read from file {}", absolutePath, e);
            throw e;
        }
    }

    /**
     * @return content of the file as string or null if the file does not exist
     * @throws IOException if something goes wrong when reading the file
     */
    @Nullable
    public String read(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Reading from file {}", absolutePath);

        if (!Files.exists(absolutePath)) {
            // do not load what's not there
            log.info("{} is not a file, does not exists or is not readable", absolutePath);
            return null;
        }

        try (InputStream inputStream = Files.newInputStream(absolutePath)) {
            return readFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read from file {}", absolutePath, e);
            throw e;
        }
    }

    @Nullable
    @Override
    public byte[] readBytes(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Reading bytes from file {}", absolutePath);

        if (!Files.exists(absolutePath)) {
            // do not load what's not there
            log.info("{} is not a file, does not exists or is not readable", absolutePath);
            return null;
        }

        return Files.readAllBytes(absolutePath);
    }

    /**
     * Writes content into a file. Will create the file if it does not exist.
     *
     * @throws IOException if something goes wrong when writing the file
     */
    public Path write(Path path, String content) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Writing into file {}", absolutePath);
        File parentDirectory = absolutePath.getParent().toFile();

        if (!parentDirectory.exists()) {
            try {
                Files.createDirectories(absolutePath.getParent());
            } catch (Exception e) {
                log.error("Could not create directories for {}", absolutePath);
                throw e;
            }
        }

        try {
            Files.writeString(absolutePath, content, CHARSET, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("Could not write into {}", absolutePath);
            throw e;
        }

        return absolutePath;
    }

    /**
     * @return whether the file is present anymore
     * @throws IOException if something goes wrong when deleting the file
     */
    public boolean delete(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        log.debug("Deleting file {}", absolutePath);
        try {
            Files.delete(absolutePath);
        } catch (NoSuchFileException e) {
            return true;
        } catch (Exception e) {
            log.error("Could not delete file {}", absolutePath, e);
            throw e;
        }

        return true;
    }
}
