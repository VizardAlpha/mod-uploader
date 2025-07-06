package com.github.argon.moduploader.core.file;

import jakarta.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * For reading, writing and deleting files and resources.
 */
public interface IFileService {

    /**
     * For writing any kind of data object as properties into a file.
     * <pre>
     *     boo.far=123
     * </pre>
     *
     * @param path where to write the property file
     * @param pojo with the data to write
     * @param <T> of the given pojo
     * @throws IOException when writing the file fails
     */
    <T> Path writeAsProperties(Path path, T pojo) throws IOException;

    /**
     * Reads content as lines from the file in the given path.
     *
     * @param path of file to read from
     * @return content as lines from the file
     * @throws IOException if something goes wrong when reading
     */
    List<String> readLines(Path path) throws IOException;

    /**
     * Reads content as string from the file in given path.
     *
     * @param path of file to read from
     * @return content as single string
     * @throws IOException if something goes wrong when reading
     */
    @Nullable
    String read(Path path) throws IOException;

    @Nullable
    byte[] readBytes(Path path) throws IOException;

    /**
     * Writes given content into file by replacing it.
     *
     * @param path of file to write into
     * @param content to write
     * @return absolute path to the written file
     * @throws IOException if something goes wrong when writing
     */
    Path write(Path path, String content) throws IOException;

    /**
     * Deletes a file under given path.
     *
     * @param path of file to delete
     * @return whether the file is present anymore
     * @throws IOException if something goes wrong when deleting
     */
    boolean delete(Path path) throws IOException;

    Path zip(Path path) throws IOException;
}
