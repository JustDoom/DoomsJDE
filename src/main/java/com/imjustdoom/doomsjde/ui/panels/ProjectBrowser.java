package com.imjustdoom.doomsjde.ui.panels;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.ui.SettingsWindow;
import com.imjustdoom.doomsjde.ui.components.ProjectButton;
import com.imjustdoom.doomsjde.ui.components.Window;
import com.imjustdoom.doomsjde.ui.panels.editor.ProjectWindow;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jspecify.annotations.NullMarked;
import java.io.File;
import java.nio.file.Path;

@NullMarked
public class ProjectBrowser extends Window {

    private final Stage stage;

    public ProjectBrowser(final JDE instance, final Stage stage) {
        super(instance);

        this.stage = stage;
    }

    public ProjectBrowser init() {
        super.init(this.stage, true);

        final Button selectButton = new ProjectButton()
                .withText("Select Project")
                .withMaxWidth(Double.MAX_VALUE)
                .withAction(_ -> {
                    final DirectoryChooser chooser = new DirectoryChooser();

                    chooser.setTitle("Project Browser");
                    chooser.setInitialDirectory(new File("."));

                    final File file = chooser.showDialog(this.stage);

                    if (file == null) { //todo() show popup if null.
                        return;
                    }

                    if (!file.isDirectory()) { //todo() show popup to warn them
                        this.logger.warning("A valid directory must have been selected!");

                        return;
                    }

                    this.stage.close();

                    try {
                        this.storageManager.addProject(file.getName(), file.toPath());

                        new ProjectWindow(this.instance, file.toPath()).init();
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

                        new ProjectWindow(this.instance, Path.of(tab.projectPath)).init();
                    });

            projects.getChildren().add(button.build());
        });

        final ScrollPane scrollPane = new ScrollPane(projects);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        final BorderPane pane = new BorderPane();

        final Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        pane.setTop(separator);

        pane.setBottom(new ProjectButton().withText("⚙").withAction(_ -> {
            new SettingsWindow(this.instance).setVisible(true);
        }).build());

        final BorderPane root = new BorderPane();

        root.setTop(selectButton);
        root.setCenter(scrollPane);
        root.setBottom(pane);
        BorderPane.setMargin(selectButton, new Insets(10));
        BorderPane.setMargin(scrollPane, new Insets(0, 10, 10, 10));
        BorderPane.setMargin(pane, new Insets(10));

        final Scene scene = new Scene(root, 512, 512);

        super.post(scene);

        this.stage.setTitle("Welcome to JDE!");
        this.stage.setScene(scene);
        this.stage.setMinWidth(512);
        this.stage.setMinHeight(512);

        this.stage.show();

        return this;
    }
}