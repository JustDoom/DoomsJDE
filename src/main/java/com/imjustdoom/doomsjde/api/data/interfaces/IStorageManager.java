package com.imjustdoom.doomsjde.api.data.interfaces;

import com.imjustdoom.doomsjde.api.data.objects.project.ProjectTab;
import org.jspecify.annotations.NullMarked;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

@NullMarked
public abstract class IStorageManager {

    protected final Path homeDirectory;
    protected final Path settingsPath;
    protected final Path projectPath;
    protected final Path workspacePath;

    protected final Logger logger;

    public IStorageManager(final Path homeDirectory, final Logger logger) {
        this.homeDirectory = homeDirectory;

        this.settingsPath = homeDirectory.resolve("settings.json");
        this.projectPath = homeDirectory.resolve("projects.json");
        this.workspacePath = homeDirectory.resolve("workspace.json");

        this.logger = logger;
    }

    public abstract void addProject(final String name, final Path projectPath);

    public abstract void removeProject(final String id);

    public abstract Map<String, ProjectTab> getProjects();

    public abstract void init();

    protected void createDirectoryIfNeeded(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(path.toString()));
        }
    }

    protected void createFileIfNeeded(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(path.toString()));
        }
    }

    protected boolean isEmpty(final Path path) {
        try {
            return Files.size(path) == 0;
        } catch (final IOException exception) {
            return true;
        }
    }
}