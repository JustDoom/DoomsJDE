package com.imjustdoom.doomsjde.api;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.imjustdoom.doomsjde.ui.ProjectSelector;
import com.imjustdoom.doomsjde.api.data.DataHandler;
import org.jspecify.annotations.NullMarked;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    public Logger getLogger() {
        return this.logger;
    }
}