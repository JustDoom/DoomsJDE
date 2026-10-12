package com.imjustdoom.doomsjde.api.data;

import com.imjustdoom.doomsjde.api.data.objects.WorkspaceState;
import org.xml.sax.SAXException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DataHandler {

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
        this.projectsFile = homePath.resolve("projects.json");
        this.workspaceFile = homePath.resolve("workspace.json");
    }

    public void init() {
        try {
            if (!Files.exists(this.homePath)) {
                Files.createDirectory(this.homePath);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(this.homePath.toString()));
        }

        try {
            if (!Files.exists(this.projectsFile)) {
                Files.createFile(this.projectsFile);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(this.projectsFile.toString()));
        }
    }

    public Map<String, String> getProjects() throws IOException {
        if (!Files.exists(this.projectsFile)) {
            return Collections.emptyMap();
        }

        if (Files.size(this.projectsFile) == 0) {
            return Collections.emptyMap();
        }

        return this.mapper.readValue(this.projectsFile, new TypeReference<>() {});
    }

    public void addProject(String name, Path projectPath) throws ParserConfigurationException, IOException, SAXException {
        init();

        final Map<String, String> projects = new HashMap<>(getProjects());
        projects.put(name, projectPath.toAbsolutePath().toString());

        this.mapper.writeValue(this.projectsFile, projects);
    }

    public Map<String, WorkspaceState> getAllWorkspaceData() throws IOException {
        if (!Files.exists(this.workspaceFile)) {
            return Collections.emptyMap();
        }

        if (Files.size(this.workspaceFile) == 0) {
            return Collections.emptyMap();
        }

        return this.mapper.readValue(this.workspaceFile, new TypeReference<>() {});
    }

    public void addWorkspaceState(String project, WorkspaceState workspaceState) throws IOException {
        init();

        final Map<String, WorkspaceState> workspaceData = new HashMap<>(getAllWorkspaceData());
        workspaceData.put(project, workspaceState);

        this.mapper.writeValue(this.workspaceFile, workspaceData);
    }
}