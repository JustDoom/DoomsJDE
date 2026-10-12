package com.imjustdoom.doomsjde.api.data;

import com.imjustdoom.doomsjde.api.data.objects.Settings;
import com.imjustdoom.doomsjde.api.data.objects.WorkspaceState;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DataHandler {
    private final Path settingsFile;
    private final Path projectsFile;
    private final Path workspaceFile;
    private final Logger logger;
    private final Path homePath;

    private final JsonMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    public DataHandler(final Logger logger, final Path homePath) {
        this.logger = logger;
        this.homePath = homePath;
        this.settingsFile = homePath.resolve("settings.json");
        this.projectsFile = homePath.resolve("projects.json");
        this.workspaceFile = homePath.resolve("workspace.json");
    }

    public void init() {
        createDirIfNeeded(this.homePath);
        createFileIfNeeded(this.settingsFile);
        createFileIfNeeded(this.projectsFile);
        createFileIfNeeded(this.workspaceFile);
    }

    private void createDirIfNeeded(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(path.toString()));
        }
    }

    private void createFileIfNeeded(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(path.toString()));
        }
    }

    public Map<String, String> getProjects() {
        if (!Files.exists(this.projectsFile)) {
            return Collections.emptyMap();
        }

        try {
            if (Files.size(this.projectsFile) == 0) {
                throw new IOException("File too empty");
            }
        } catch (IOException _) {
            return Collections.emptyMap();
        }

        return this.mapper.readValue(this.projectsFile, new TypeReference<>() {});
    }

    public void addProject(String name, Path projectPath) {
        init();

        final Map<String, String> projects = new HashMap<>(getProjects());
        projects.put(name, projectPath.toAbsolutePath().toString());

        this.mapper.writeValue(this.projectsFile, projects);
    }

    public Map<String, WorkspaceState> getAllWorkspaceData() {
        if (!Files.exists(this.workspaceFile)) {
            return Collections.emptyMap();
        }

        try {
            if (Files.size(this.workspaceFile) == 0) {
                throw new IOException("File too empty");
            }
        } catch (IOException _) {
            return Collections.emptyMap();
        }

        return this.mapper.readValue(this.workspaceFile, new TypeReference<>() {});
    }

    public void addWorkspaceState(String project, WorkspaceState workspaceState) {
        init();

        final Map<String, WorkspaceState> workspaceData = new HashMap<>(getAllWorkspaceData());
        workspaceData.put(project, workspaceState);

        this.mapper.writeValue(this.workspaceFile, workspaceData);
    }

    public Settings getSettings() {
        if (!Files.exists(this.settingsFile)) {
            return new Settings();
        }

        try {
            if (Files.size(this.settingsFile) == 0) {
                throw new IOException("File too empty");
            }
        } catch (IOException _) {
            return new Settings();
        }

        return this.mapper.readValue(this.settingsFile, Settings.class);
    }

    public void setSettings(Settings settings) {
        init();

        this.mapper.writeValue(this.settingsFile, settings);
    }
}