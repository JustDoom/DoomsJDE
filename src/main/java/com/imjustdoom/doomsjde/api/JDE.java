package com.imjustdoom.doomsjde.api;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.imjustdoom.doomsjde.ui.ProjectSelector;
import com.imjustdoom.doomsjde.api.data.DataHandler;
import org.jspecify.annotations.NullMarked;
import javax.swing.*;
import java.util.logging.Logger;

@NullMarked
public final class JDE {

    private final DataHandler dataHandler;
    private final Logger logger;

    public JDE() {
        this.dataHandler = new DataHandler(this.logger = Logger.getLogger("JDE"));
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