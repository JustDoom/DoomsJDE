package com.imjustdoom.doomsjde.ui.components;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.enums.ImageIcons;
import com.imjustdoom.doomsjde.ui.panels.ProjectBrowser;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jspecify.annotations.NullMarked;
import java.net.URL;
import java.util.logging.Logger;

@NullMarked
public abstract class Window {

    protected final StorageManager storageManager;
    protected final Logger logger;
    protected final JDE instance;

    public Window(final JDE instance) {
        this.instance = instance;
        this.storageManager = this.instance.getStorageManager();
        this.logger = this.instance.getLogger();
    }

    public void init(final Stage stage, final boolean isBrowser) {
        this.instance.readLogo(ImageIcons.LOGO).ifPresent(logo -> stage.getIcons().add(logo));

        stage.setOnHidden(_ -> {
            stage.setScene(null);
        });

        stage.setOnCloseRequest(_ -> {
            stage.close();

            if (isBrowser) {
                return;
            }

            new ProjectBrowser(this.instance, new Stage()).init();
        });
    }

    public void post(final Scene scene) {
        final URL text = getClass().getClassLoader().getResource("global.css");

        if (text == null) {
            return;
        }

        scene.getStylesheets().add(text.toExternalForm());
    }
}