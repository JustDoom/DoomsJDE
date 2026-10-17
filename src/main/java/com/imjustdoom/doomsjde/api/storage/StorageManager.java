package com.imjustdoom.doomsjde.api.storage;

import com.imjustdoom.doomsjde.api.storage.interfaces.IStorageManager;
import com.imjustdoom.doomsjde.api.storage.objects.Settings;
import com.imjustdoom.doomsjde.api.storage.objects.WorkspaceState;
import com.imjustdoom.doomsjde.api.storage.objects.project.ProjectTab;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@NullMarked
public class StorageManager extends IStorageManager {

    private final JsonMapper instance = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    public StorageManager(final Path homeDirectory, final Logger logger) {
        super(homeDirectory, logger);
    }

    @Override
    public void addProject(final String name, final Path projectPath) {
        init();

        final Map<String, ProjectTab> projects = new HashMap<>(getProjects());

        final ProjectTab project = new ProjectTab();

        project.projectName = name;
        project.projectPath = projectPath.toAbsolutePath().toString();

        projects.put(UUID.ofEpochMillis(Instant.now().toEpochMilli()).toString(), project);

        this.instance.writeValue(this.projectPath, projects);
    }

    @Override
    public void removeProject(final String id) {

    }

    @Override
    public Map<String, ProjectTab> getProjects() {
        if (!Files.exists(this.projectPath)) {
            return Collections.emptyMap();
        }

        if (isEmpty(this.projectPath)) {
            return Collections.emptyMap();
        }

        return this.instance.readValue(this.projectPath, new TypeReference<>() {});
    }

    @Override
    public void init() {
        createDirectoryIfNeeded(this.homeDirectory);
        createFileIfNeeded(this.settingsPath);
        createFileIfNeeded(this.projectPath);
        createFileIfNeeded(this.workspacePath);
    }

    public Map<String, WorkspaceState> getAllWorkspaceData() {
        if (!Files.exists(this.workspacePath)) {
            return Collections.emptyMap();
        }

        if (isEmpty(this.workspacePath)) {
            return Collections.emptyMap();
        }

        return this.instance.readValue(this.workspacePath, new TypeReference<>() {});
    }

    public void addWorkspaceState(String project, WorkspaceState workspaceState) {
        init();

        final Map<String, WorkspaceState> workspaceData = new HashMap<>(getAllWorkspaceData());
        workspaceData.put(project, workspaceState);

        this.instance.writeValue(this.workspacePath, workspaceData);
    }

    public Settings getSettings() {
        if (!Files.exists(this.settingsPath)) {
            return new Settings();
        }

        if (isEmpty(this.settingsPath)) {
            return new Settings();
        }

        return this.instance.readValue(this.settingsPath, Settings.class);
    }

    public void setSettings(Settings settings) {
        init();

        this.instance.writeValue(this.settingsPath, settings);
    }
}