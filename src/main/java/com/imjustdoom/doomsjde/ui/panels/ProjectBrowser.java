package com.imjustdoom.doomsjde.ui.panels;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.ui.ProjectWindow;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ProjectBrowser {
    private final JDE instance;
    private final StorageManager storageManager;
    private final Logger logger;
    private final Stage stage;

    public ProjectBrowser(@NonNull final JDE instance) {
        this.instance = instance;
        this.storageManager = this.instance.getStorageManager();
        this.logger = this.instance.getLogger();
        this.stage = new Stage();
        init();
    }

    public ProjectBrowser init() {
        final Button selectButton = new Button("Select Project");
        selectButton.setMaxWidth(Double.MAX_VALUE);
        selectButton.setOnAction(_ -> {
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
        });

        final VBox projects = new VBox();
        projects.setSpacing(8);
        projects.setPadding(new Insets(10));

        final int MAX_BUTTON_HEIGHT = 40;

        this.storageManager.getProjects().forEach((_, tab) -> {
            final Button button = new Button("%s - %s".formatted(tab.projectName, tab.projectPath));
            button.setMaxWidth(Double.MAX_VALUE);
            button.setPrefHeight(MAX_BUTTON_HEIGHT);
            button.setMinHeight(MAX_BUTTON_HEIGHT);
            button.setOnAction(_ -> {
                this.stage.close();
                try {
                    new ProjectWindow(this.instance, new File(tab.projectPath)).setVisible(true);
                } catch (final IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
            projects.getChildren().add(button);
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
        this.stage.setTitle("Project Selector");
        this.stage.setScene(scene);
        this.stage.setMinWidth(512);
        this.stage.setMinHeight(512);

//        this.instance.readLogo(ImageIcons.LOGO).ifPresent(image ->
//                this.stage.getIcons().add(image)
//        );

        return this;
    }

    public void show() {
        this.stage.show();
    }
}