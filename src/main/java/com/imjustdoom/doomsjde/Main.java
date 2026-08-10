package com.imjustdoom.doomsjde;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import javax.swing.*;

// It actually saves!
public class Main {
    void main() {
        FlatDraculaIJTheme.setup();
        SwingUtilities.invokeLater(() -> {
            new ProjectSelector().setVisible(true);
        });
    }
}
