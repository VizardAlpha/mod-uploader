package com.github.argon.moduploader.command;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;


@CommandLine.Command
@ApplicationScoped
public class CurrentWorkingDir {
    private Path path = Paths.get(System.getProperty("user.dir"));

    @CommandLine.Option(names = {"-cwd", "--current-working-dir"})
    public void setPath(String path) {
        this.path = Paths.get(path);
    }

    public Path getPath() {
        return path;
    }
}
