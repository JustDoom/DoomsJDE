package com.imjustdoom.doomsjde.ui.panels.editor;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.ui.components.Window;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectWindow extends Window {

    private final Path directory;
    private final Stage stage;

    public ProjectWindow(final JDE instance, final Path directory) {
        super(instance);

        // window stuff
        this.directory = directory;
        this.stage = new Stage();
    }

    public ProjectWindow init() {
        super.init(this.stage, false);

        final ListView<String> view = new ListView<>();

        try (final Stream<Path> paths = Files.list(this.directory)) {
            for (final Path path : paths.collect(Collectors.toSet())) {
                view.getItems().add(path.getFileName().toString());
            }
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }

        // all logic not relating to the building the menu go above.
        final VBox root = new VBox(view);

        root.setSpacing(8);
        root.setPadding(new Insets(10));

        final ScrollPane scrollPane = new ScrollPane(view);

        scrollPane.setFitToWidth(true);

        final BorderPane borderPane = new BorderPane();

        borderPane.setLeft(scrollPane);

        final Scene scene = new Scene(borderPane, 50, 300);

        this.stage.setScene(scene);
        // fills the full window, we can retain resolution changes later.
        this.stage.setMaximized(true);

        this.stage.show();

        return this;
    }
}