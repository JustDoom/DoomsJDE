package com.imjustdoom.doomsjde;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static JDEDataHandler DATA_HANDLER = new JDEDataHandler();

    void main() {
        FlatDraculaIJTheme.setup();
        SwingUtilities.invokeLater(() -> new ProjectSelector().setVisible(true));
    }
}
