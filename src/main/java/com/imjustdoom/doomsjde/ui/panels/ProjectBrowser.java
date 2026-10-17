package com.imjustdoom.doomsjde.ui.panels;

import com.imjustdoom.doomsjde.api.enums.ImageIcons;
import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.storage.StorageManager;
import com.imjustdoom.doomsjde.ui.ProjectWindow;
import com.imjustdoom.doomsjde.ui.objects.Island;
import org.jspecify.annotations.NonNull;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ProjectBrowser extends JFrame {

    private final JDE instance;
    private final StorageManager storageManager;
    private final Logger logger;

    public ProjectBrowser(@NonNull final JDE instance) {
        this.instance = instance;
        this.storageManager = this.instance.getStorageManager();
        this.logger = this.instance.getLogger();
    }

    public ProjectBrowser init() {
        final JPanel island = new Island()
                .withBorderLayout()
                .withButton(button -> {
                    button.setText("Select Project");

                    button.addActionListener(_ -> {
                        final JFileChooser chooser = new JFileChooser();

                        chooser.setCurrentDirectory(new java.io.File("."));

                        chooser.setDialogTitle("Project Browser");

                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        chooser.setAcceptAllFileFilterUsed(false);

                        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                            return;
                        }

                        if (!chooser.getSelectedFile().isDirectory()) {
                            this.logger.warning("A valid directory must have been selected!");

                            return;
                        }

                        dispose();

                        try {
                            this.storageManager.addProject(chooser.getSelectedFile().getName(), Path.of(chooser.getSelectedFile().getAbsolutePath()));

                            new ProjectWindow(this.instance, chooser.getSelectedFile()).setVisible(true);
                        } catch (final Exception exception) {
                            throw new RuntimeException(exception);
                        }
                    });
                }, BorderLayout.NORTH);

        int MAX_BUTTON_HEIGHT = 40;

        final JPanel projects = new Island().withBoxLayout(BoxLayout.Y_AXIS).withIsland(builder -> {
            this.storageManager.getProjects().forEach((_, tab) -> {
                builder.withButton(button -> {
                    button.setText("%s - %s".formatted(tab.projectName, tab.projectPath));
                    button.setIcon(null);

                    button.addActionListener(_ -> {
                        dispose();

                        try {
                            new ProjectWindow(this.instance, new File(tab.projectPath)).setVisible(true);
                        } catch (final IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });

                    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, MAX_BUTTON_HEIGHT));
                    button.setPreferredSize(new Dimension(0, MAX_BUTTON_HEIGHT));
                    button.setAlignmentX(Component.LEFT_ALIGNMENT);
                });
            });
        });

        island.add(new JScrollPane(projects), BorderLayout.CENTER);

        setContentPane(island);

        setTitle("Project Selector");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(512, 512));

        this.instance.readLogo(ImageIcons.LOGO).ifPresent(this::setIconImage);

        //this reduces the windows to fit the size of the content, so minimum size above is nice to have.
        pack();

        setLocationRelativeTo(null);

        return this;
    }
}