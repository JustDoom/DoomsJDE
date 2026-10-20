package com.imjustdoom.doomsjde.api;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.ThemeManager;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.data.objects.Settings;
import com.imjustdoom.doomsjde.api.enums.ImageIcons;
import com.imjustdoom.doomsjde.ui.panels.ProjectBrowser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

@NullMarked
public final class JDE extends Application {

    private final StorageManager storageManager;

    private final Path directory;
    private final Logger logger;

    private Settings settings;

    public JDE() {
        this.storageManager = new StorageManager(
                this.directory = Paths.get(System.getProperty("user.home")).resolve(".jde"),
                this.logger = Logger.getLogger("JDE")
        );

        this.settings = new Settings();
    }

    @Override
    public void init() {
        this.storageManager.init();

        this.settings = this.storageManager.getSettings();
    }

    @Override
    public void start(final Stage stage) {
        Platform.setImplicitExit(false);
        /*FlatDraculaIJTheme.setup();

        final String selectedFont = "JetBrains Mono"; // Will hook this up to settings later
        boolean hasFont = false;
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String f : fonts) {
            if (f.equals(selectedFont)) {
                hasFont = true;
                break;
            }
        }

        if (!hasFont) {
            JOptionPane.showMessageDialog(null, "Unable to find the font '" + selectedFont + "', a default font will be used.");
        }

        UIManager.put("defaultFont", new Font(selectedFont, Font.PLAIN, 14));*/

        //todo() make custom theme(let me have fun)
        switch (this.settings.theme) {
            case "dark" -> ThemeManager.instance().setTheme(new PrimerDark());
            case "light" -> ThemeManager.instance().setTheme(new PrimerLight());
            case "system" -> ThemeManager.useDefault();
        }

        new ProjectBrowser(this, stage).init();
    }

    public Optional<Image> readLogo(final ImageIcons icon) {
        Image image = null;

        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(icon.getPath())) {
            if (inputStream != null) {
                image = new Image(inputStream);
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        return Optional.ofNullable(image);
    }

    public StorageManager getStorageManager() {
        return this.storageManager;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Path getDirectory() {
        return this.directory;
    }

    public Logger getLogger() {
        return this.logger;
    }
}