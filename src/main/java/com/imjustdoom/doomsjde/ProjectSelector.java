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
    public static JDEDataHandler DATA_HANDLER = new JDEDataHandler();

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
                DATA_HANDLER.addProject(chooser.getSelectedFile().getName(), Path.of(chooser.getSelectedFile().getAbsolutePath()));
                new Editor(chooser.getSelectedFile()).setVisible(true);
            } catch (IOException | ParserConfigurationException | TransformerException e) {
                throw new RuntimeException(e);
            }
        });
        content.add(projectSelector, BorderLayout.NORTH);

        int MAX_BUTTON_HEIGHT = 40;

        JPanel projectsPanel = new JPanel();
        projectsPanel.setLayout(new BoxLayout(projectsPanel, BoxLayout.Y_AXIS));

        try {
            for (Map.Entry<String, String> project : DATA_HANDLER.getProjects().entrySet()) {
                JButton button = createProjectButton(project, MAX_BUTTON_HEIGHT);

                projectsPanel.add(button);
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

    private JButton createProjectButton(Map.Entry<String, String> project, int MAX_BUTTON_HEIGHT) {
        JButton button = new JButton(project.getKey() + " - " + project.getValue());
        button.addActionListener(_ -> {
            dispose();
            try {
                new Editor(new File(project.getValue())).setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, MAX_BUTTON_HEIGHT));
        button.setPreferredSize(new Dimension(0, MAX_BUTTON_HEIGHT));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }
}
