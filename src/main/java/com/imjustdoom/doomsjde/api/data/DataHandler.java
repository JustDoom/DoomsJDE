package com.imjustdoom.doomsjde.api.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DataHandler {
    private static final FileSystemView FILE_SYSTEM = FileSystemView.getFileSystemView();
    private static final Path HOME = Paths.get(FILE_SYSTEM.getHomeDirectory().getAbsolutePath(), "/.jde");

    private final Logger logger;

    public DataHandler(final Logger logger) {
        this.logger = logger;
    }

    public void init() {
        try {
            if (!Files.exists(HOME)) {
                Files.createDirectory(HOME);
            }
        } catch (final Exception exception) {
            this.logger.warning("Unable to create the home folder!");
        }
    }

    private Document readXML(String path) throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = new File(path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try {
            return builder.parse(xmlFile);
        } catch (FileNotFoundException _) {
            return null;
        }
    }

    private void saveXML(Document document, String filePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    public Map<String, String> getProjects() throws ParserConfigurationException, IOException, SAXException {
        Document document = readXML("projects.xml");

        if (document == null) {
            return Collections.emptyMap();
        }

        Element root = document.getDocumentElement();
        root.normalize();

        NodeList projects = root.getElementsByTagName("project");

        Map<String, String> projectMap = new HashMap<>(projects.getLength());
        for (int i = 0; i < projects.getLength(); i++) {
            Node node = projects.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element project = (Element) node;
                projectMap.put(project.getAttribute("name"),
                        project.getElementsByTagName("path").item(0).getTextContent());
            }
        }
        return projectMap;
    }

    public void addProject(String name, Path path) throws ParserConfigurationException, TransformerException {
        Map<String, String> existingProjects;
        try {
            existingProjects = getProjects();
        } catch (IOException | SAXException e) {
            existingProjects = Collections.emptyMap();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element projects = document.createElement("projects");
        document.appendChild(projects);

        for (Map.Entry<String, String> entry : existingProjects.entrySet()) {
            Element project = document.createElement("project");
            project.setAttribute("name", entry.getKey());
            projects.appendChild(project);

            Element projectPathElement = document.createElement("path");
            projectPathElement.setTextContent(entry.getValue());
            project.appendChild(projectPathElement);
        }

        Element project = document.createElement("project");
        project.setAttribute("name", name);
        projects.appendChild(project);

        Element projectPathElement = document.createElement("path");
        projectPathElement.setTextContent(String.valueOf(path.toAbsolutePath()));
        project.appendChild(projectPathElement);

        saveXML(document, "projects.xml");
    }
}
