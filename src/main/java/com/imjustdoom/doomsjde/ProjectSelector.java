package com.imjustdoom.doomsjde;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ProjectSelector extends JFrame {
    public ProjectSelector() {
        JPanel content = new JPanel();

        JButton projectSelector = new JButton("Select Project");
        projectSelector.addActionListener(_ -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Project Selector");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                System.out.println(chooser.getCurrentDirectory()); // Parent directory
                System.out.println(chooser.getSelectedFile()); // Selected directory

                if (!chooser.getSelectedFile().isDirectory()) {
                    System.err.println("A valid directory must have been selected");
                    return;
                }

                dispose();
                try {
                    new Editor(chooser.getSelectedFile()).setVisible(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        content.add(projectSelector);

        setContentPane(content);
        setTitle("Project Selector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
}
