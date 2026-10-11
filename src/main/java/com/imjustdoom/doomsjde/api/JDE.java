package com.imjustdoom.doomsjde.api;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.imjustdoom.doomsjde.api.data.DataHandler;
import com.imjustdoom.doomsjde.ui.ProjectSelector;
import org.jspecify.annotations.NullMarked;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

@NullMarked
public final class JDE {

    private final DataHandler dataHandler;
    private final Path directory;
    private final Logger logger;

    public JDE() {
        this.dataHandler = new DataHandler(this.logger = Logger.getLogger("JDE"), this.directory = Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath(), "/.jde"));
    }

    public void init() {
        this.dataHandler.init();

        FlatDraculaIJTheme.setup();
        SwingUtilities.invokeLater(() -> new ProjectSelector(this).setVisible(true));
    }

    public Optional<BufferedImage> readLogo(final String path) {
        BufferedImage image = null;

        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream != null) {
                image = ImageIO.read(inputStream);
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        return Optional.ofNullable(image);
    }

    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    public Logger getLogger() {
        return this.logger;
    }
}