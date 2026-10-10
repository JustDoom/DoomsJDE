package com.imjustdoom.doomsjde.adapters;

import com.imjustdoom.doomsjde.Editor;
import com.imjustdoom.doomsjde.ProjectSelector;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowCloseAdapter extends WindowAdapter {

    private final Editor editor;

    public WindowCloseAdapter(final Editor editor) {
        this.editor = editor;
    }

    @Override
    public void windowClosing(final WindowEvent event) {
        this.editor.dispose();

        SwingUtilities.invokeLater(() -> new ProjectSelector().setVisible(true));
    }
}