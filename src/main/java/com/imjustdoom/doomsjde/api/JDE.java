package com.imjustdoom.doomsjde.api;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.data.objects.Settings;
import com.imjustdoom.doomsjde.ui.ImageIcons;
import com.imjustdoom.doomsjde.ui.panels.ProjectBrowser;
import org.jspecify.annotations.NullMarked;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

@NullMarked
public final class JDE {

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

    public void init() {
        this.storageManager.init();

        this.settings = this.storageManager.getSettings();

        FlatDraculaIJTheme.setup();
        SwingUtilities.invokeLater(() -> new ProjectBrowser(this).init().setVisible(true));
    }

    public Optional<BufferedImage> readLogo(final ImageIcons icon) {
        BufferedImage image = null;

        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(icon.getPath() + ".png")) {
            if (inputStream != null) {
                image = ImageIO.read(inputStream);
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

    public Logger getLogger() {
        return this.logger;
    }
}