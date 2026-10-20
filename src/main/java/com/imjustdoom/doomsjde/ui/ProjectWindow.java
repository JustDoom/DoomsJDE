package com.imjustdoom.doomsjde.ui;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.constants.Colours;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.data.objects.WorkspaceState;
import com.imjustdoom.doomsjde.api.enums.ImageIcons;
import com.imjustdoom.doomsjde.ui.adapters.WindowCloseAdapter;
import com.imjustdoom.doomsjde.ui.objects.JEditor;
import com.imjustdoom.doomsjde.ui.objects.JFileTree;
import com.imjustdoom.doomsjde.ui.objects.JTerminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private final JSplitPane terminalEditorSplitPane;

    private final JToolBar toolbar;
    private final JEditor editor;
    private final JFileTree fileTree;
    private final JTerminal terminal;

    public ProjectWindow(JDE instance, File directory) throws IOException {
        this.instance = instance;
        this.logger = this.instance.getLogger();
        this.storageManager = this.instance.getStorageManager();

        // UI setup - Create the elements
        JPanel panel = new JPanel(new BorderLayout());

        this.toolbar = new JToolBar();
        this.toolbar.setFloatable(false);
        this.toolbar.setRollover(true);

        this.editor = new JEditor(this.instance);

        this.fileTree = new JFileTree(this, directory);
        // Can block the thread on a large project, maybe a good idea to put this on another thread and have a loading bar
        this.fileTree.init();

        this.terminal = new JTerminal();
        this.terminal.setEditable(false);

        // UI setup - configure the element placement
        this.projectEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.fileTree, this.editor);
        this.projectEditorSplitPane.setResizeWeight(0.2);
        this.projectEditorSplitPane.setOneTouchExpandable(true);
        this.projectEditorSplitPane.setDividerLocation(200);

        this.terminalEditorSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.projectEditorSplitPane, this.terminal);
        this.terminalEditorSplitPane.setResizeWeight(0.75);
        this.terminalEditorSplitPane.setOneTouchExpandable(true);

        this.toolbar.add(button("⚙", Color.lightGray, _ -> new SettingsWindow(this.instance).setVisible(true)));
        this.toolbar.addSeparator();
        this.toolbar.add(button("▶", _ -> runProject()));

        panel.add(this.toolbar, BorderLayout.NORTH);
        panel.add(this.terminalEditorSplitPane, BorderLayout.CENTER);

        // Run after UI setup as it modifies the UI
        setupState(directory, this.storageManager.getAllWorkspaceData().get(directory.getName()));

        setContentPane(panel);

        setMinimumSize(new Dimension(1024, 1024)); // temporary dimension size

        // Do nothing, so we can re-open the projects window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowCloseAdapter(this.instance, this));

        setTitle("Dooms JDE");
        //this.instance.readLogo(ImageIcons.LOGO).ifPresent(this::setIconImage);
        setupKeybinds();
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
            this.projectEditorSplitPane.setRightComponent(new JLabel("<html><div align='center'>" + text + "</div></html>", SwingConstants.CENTER));
            this.logger.severe(text);
        }
    }

    /**
     * Sets the last used workspace state. Such as open files
     */
    private void setupState(File directory, WorkspaceState state) {
        if (state == null) {
            // Don't show a blank editor area when a new project is opened with no opened file history
            this.projectEditorSplitPane.setRightComponent(new JLabel("<html><div align='center'>No file selected</div></html>", SwingConstants.CENTER));
        } else {
            Path path = Path.of(directory.getAbsoluteFile() + state.lastFile());
            if (Files.exists(path)) {
                setEditorFile(directory, path.toFile());
            } else {
                final String text = String.format("Unable to find the file '%s' :(\nPlease make sure it exists", path.toAbsolutePath());
                this.projectEditorSplitPane.setRightComponent(new JLabel("<html><div align='center'>" + text + "</div></html>", SwingConstants.CENTER));
            }
        }
    }

    private void setupKeybinds() {
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "run");
        getRootPane().getActionMap().put("run", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runProject();
            }
        });
    }

    // TODO: Actually kill any alive processes when the IDE window closes
    private void runProject() {
        this.terminal.setText("");
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
                        this.terminal.setText(this.terminal.getText() + "\n" + line);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, "gradle-task").start();
    }

    private JButton button(final ImageIcons icon, final int width, final int height, final ActionListener listener) {
        //JButton button = new JButton(
        //        new ImageIcon(this.instance.readLogo(icon).get().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING)));
        //button.addActionListener(listener);
        //return button;
        return null;
    }

    private JButton button(final String text, final ActionListener listener) {
        return button(text, Colours.NICE_GREEN, listener);
    }

    private JButton button(final String text, final Color colour, final ActionListener listener) {
        JButton button = new JButton(text);
        button.setForeground(colour);
        button.addActionListener(listener);
        return button;
    }
}
