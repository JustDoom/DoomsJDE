package com.imjustdoom.doomsjde.api.data;

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
    private final Logger logger;
    private final Path path;

    private final JsonMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    public DataHandler(final Logger logger, final Path path) {
        this.logger = logger;
        this.path = path;
        this.projectsFile = path.resolve("projects.json");
    }

    public void init() {
        try {
            if (!Files.exists(this.path)) {
                Files.createDirectory(this.path);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(this.path.toString()));
        }

        try {
            if (!Files.exists(this.projectsFile)) {
                Files.createFile(this.projectsFile);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create %s".formatted(this.projectsFile.toString()));
        }
    }

    public Map<String, String> getProjects() throws ParserConfigurationException, IOException, SAXException {
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
}