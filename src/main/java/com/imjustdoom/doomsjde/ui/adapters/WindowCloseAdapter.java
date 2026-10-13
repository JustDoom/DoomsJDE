package com.imjustdoom.doomsjde.ui.adapters;

import com.imjustdoom.doomsjde.ui.ProjectWindow;
import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.ui.ProjectSelector;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowCloseAdapter extends WindowAdapter {

    private final JDE instance;
    private final ProjectWindow editor;

    public WindowCloseAdapter(final JDE instance, final ProjectWindow editor) {
        this.instance = instance;
        this.editor = editor;
    }

    @Override
    public void windowClosing(final WindowEvent event) {
        this.editor.dispose();

        SwingUtilities.invokeLater(() -> new ProjectSelector(this.instance).setVisible(true));
    }
}