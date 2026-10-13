package com.imjustdoom.doomsjde.ui;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.data.objects.WorkspaceState;
import com.imjustdoom.doomsjde.ui.adapters.WindowCloseAdapter;
import com.imjustdoom.doomsjde.ui.objects.JEditor;
import com.imjustdoom.doomsjde.ui.objects.JFileTree;
import com.imjustdoom.doomsjde.ui.objects.JTerminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ProjectWindow extends JFrame {
    private final StorageManager storageManager;
    private final Logger logger;
    private final JDE instance;

    private final JSplitPane projectEditorSplitPane;
    private final JSplitPane terminalEditorSplitPlane;
    private final JEditor editor;
    private final JTerminal terminal;

    public ProjectWindow(JDE instance, File directory) throws IOException {
        this.instance = instance;
        this.logger = this.instance.getLogger();
        this.storageManager = this.instance.getStorageManager();

        JPanel panel = new JPanel(new BorderLayout());

        this.editor = new JEditor(this.instance);

        this.projectEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, this.editor);
        this.projectEditorSplitPane.setResizeWeight(0.2);
        this.projectEditorSplitPane.setOneTouchExpandable(true);
        this.projectEditorSplitPane.setDividerLocation(200);

        this.terminal = new JTerminal();
        this.terminal.setEditable(false);
        this.terminalEditorSplitPlane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.projectEditorSplitPane, this.terminal);
        this.terminalEditorSplitPlane.setResizeWeight(0.75);
        this.terminalEditorSplitPlane.setOneTouchExpandable(true);

        // I don't like this, clean it up later
        WorkspaceState workspaceState = this.storageManager.getAllWorkspaceData().get(directory.getName());
        if (workspaceState == null) {
            // Don't show a blank editor area when a new project is opened with no opened file history
            this.projectEditorSplitPane.setRightComponent(new JLabel("<html><div align='center'>No file selected</div></html>", SwingConstants.CENTER));
        } else {
            Path path = Path.of(directory.getAbsoluteFile() + workspaceState.lastFile());
            if (Files.exists(path)) {
                try {
                    this.editor.setEditorFile(directory, path.toFile());
                    this.projectEditorSplitPane.setRightComponent(this.editor);
                } catch (IOException exception) {
                    final String text = String.format("Unable to read the file '%s', it will need a Reader written for it or will remain unsupported", path.getFileName());
                    this.projectEditorSplitPane.setRightComponent(new JLabel("<html><div align='center'>" + text + "</div></html>", SwingConstants.CENTER));
                    this.logger.severe(text);
                }
            } else {
                final String text = String.format("Unable to find the file '%s' :(\nPlease make sure it exists", path.toAbsolutePath());
                this.projectEditorSplitPane.setRightComponent(new JLabel("<html><div align='center'>" + text + "</div></html>", SwingConstants.CENTER));
            }
        }

        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "run");
        getRootPane().getActionMap().put("run", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminal.setText("");
                new Thread(() -> {
                    try {
                        final String os = System.getProperty("os.name");
                        ProcessBuilder builder = new ProcessBuilder((os.contains("Windows") ? "gradlew.bat" : "./gradlew"), "run").redirectErrorStream(true);

                        // Grab the Java version that is running the IDE itself
                        // TODO: Make configurable
                        String javaHome = System.getProperty("java.home");
                        if (javaHome != null) {
                            builder.environment().put("JAVA_HOME", javaHome);
                        }

                        Process process = builder.start();

                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                terminal.setText(terminal.getText() + "\n" + line);
                            }
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }, "gradle-task").start();
            }
        });

        JFileTree fileTree = new JFileTree(this, directory);
        fileTree.init();
        this.projectEditorSplitPane.setLeftComponent(fileTree);
        panel.add(this.terminalEditorSplitPlane, BorderLayout.CENTER);

        setContentPane(panel);
        setTitle("Dooms JDE");

        setMinimumSize(new Dimension(1024, 1024)); // temporary dimension size

        this.instance.readLogo("icon.png").ifPresent(this::setIconImage);

        //Do nothing, so we can re-open the projects window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowCloseAdapter(this.instance, this));

        pack();
        setLocationRelativeTo(null);
    }

    public void setEditorFile(File directory, File file) {
        try {
            this.editor.setEditorFile(directory, file);
            this.projectEditorSplitPane.setRightComponent(this.editor);
        } catch (IOException exception) {
            // Unable to read the file type :(
            final String text = String.format("Unable to read the file '%s', it will need a Reader written for it or will remain unsupported", file.getName());
            // HTML so the label wraps to the width it's given
            JLabel label = new JLabel("<html><div align='center'>" + text + "</div></html>", SwingConstants.CENTER);
            this.projectEditorSplitPane.setRightComponent(label);
            this.logger.severe(text);
        }
    }
}
