package com.imjustdoom.doomsjde;

import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ProjectSelector extends JFrame {
    public ProjectSelector() {
        JPanel content = new JPanel(new BorderLayout());

        JButton projectSelector = new JButton("Select Project");
        projectSelector.addActionListener(_ -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Project Selector");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            if (!chooser.getSelectedFile().isDirectory()) {
                System.err.println("A valid directory must have been selected");
                return;
            }

            dispose();
            try {
                Main.DATA_HANDLER.addProject(chooser.getSelectedFile().getName(), Path.of(chooser.getSelectedFile().getAbsolutePath()));
                new Editor(chooser.getSelectedFile()).setVisible(true);
            } catch (IOException | ParserConfigurationException | TransformerException e) {
                throw new RuntimeException(e);
            }
        });
        content.add(projectSelector, BorderLayout.NORTH);

        JPanel projectsPanel = new JPanel(new GridLayout(0, 1));
        try {
            for (Map.Entry<String, String> project : Main.DATA_HANDLER.getProjects().entrySet()) {
                projectsPanel.add(new JButton(project.getKey() + " - " + project.getValue()));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println(e.getMessage());
        }

        JScrollPane scrollableProjects = new JScrollPane(projectsPanel);
        content.add(scrollableProjects, BorderLayout.CENTER);

        setContentPane(content);
        setTitle("Project Selector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
}
