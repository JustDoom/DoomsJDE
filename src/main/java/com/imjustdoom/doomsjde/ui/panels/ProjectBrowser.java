package com.imjustdoom.doomsjde.ui.panels;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.enums.ImageIcons;
import com.imjustdoom.doomsjde.ui.ProjectWindow;
import com.imjustdoom.doomsjde.ui.components.ProjectButton;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jspecify.annotations.NullMarked;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@NullMarked
public class ProjectBrowser {
    
    private final JDE instance;
    private final StorageManager storageManager;
    private final Logger logger;
    private final Stage stage;

    public ProjectBrowser(final JDE instance, final Stage stage) {
        this.instance = instance;
        this.storageManager = this.instance.getStorageManager();
        this.logger = this.instance.getLogger();

        this.stage = stage;
    }

    public ProjectBrowser init() {
        final Button selectButton = new ProjectButton()
                .withText("Select Project")
                .withMaxWidth(Double.MAX_VALUE)
                .withAction(_ -> {
                    final DirectoryChooser chooser = new DirectoryChooser();

                    chooser.setTitle("Project Browser");
                    chooser.setInitialDirectory(new File("."));

                    final File selected = chooser.showDialog(this.stage);

                    if (selected == null) return;

                    if (!selected.isDirectory()) {
                        this.logger.warning("A valid directory must have been selected!");

                        return;
                    }

                    this.stage.close();

                    try {
                        this.storageManager.addProject(selected.getName(), Path.of(selected.getAbsolutePath()));
                        new ProjectWindow(this.instance, selected).setVisible(true);
                    } catch (final Exception exception) {
                        throw new RuntimeException(exception);
                    }
                }).build();

        final VBox projects = new VBox();

        projects.setSpacing(8);
        projects.setPadding(new Insets(10));

        final int MAX_BUTTON_HEIGHT = 40;

        this.storageManager.getProjects().forEach((_, tab) -> {
            final ProjectButton button = new ProjectButton()
                    .withText("%s - %s".formatted(tab.projectName, tab.projectPath))
                    .withMaxWidth(Double.MAX_VALUE)
                    .withPrefHeight(MAX_BUTTON_HEIGHT)
                    .withMinHeight(MAX_BUTTON_HEIGHT)
                    .withAction(_ -> {
                        this.stage.close();

                        try {
                            new ProjectWindow(this.instance, new File(tab.projectPath)).setVisible(true);
                        } catch (final IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });

            projects.getChildren().add(button.build());
        });

        final ScrollPane scrollPane = new ScrollPane(projects);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        final BorderPane root = new BorderPane();

        root.setTop(selectButton);
        root.setCenter(scrollPane);
        BorderPane.setMargin(selectButton, new Insets(10));
        BorderPane.setMargin(scrollPane, new Insets(0, 10, 10, 10));

        final Scene scene = new Scene(root, 512, 512);

        this.instance.readLogo(ImageIcons.LOGO).ifPresent(logo -> this.stage.getIcons().add(logo));

        this.stage.setTitle("Welcome to JDE!");
        this.stage.setScene(scene);
        this.stage.setMinWidth(512);
        this.stage.setMinHeight(512);

        this.stage.show();

        return this;
    }
}