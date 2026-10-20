package com.imjustdoom.doomsjde.ui;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.data.StorageManager;
import com.imjustdoom.doomsjde.api.data.objects.Settings;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

public class SettingsWindow extends JFrame {
    private final StorageManager storageManager;
    private final Logger logger;
    private final JDE instance;

    private final JSpinner zoomLevel = new JSpinner(new SpinnerNumberModel(14, 6, 48, 1));
    private final JSpinner marginLine = new JSpinner(new SpinnerNumberModel(80, 0, 255, 1));
    private final JCheckBox visibleWhitespace = new JCheckBox();
    private final JComboBox<String> theme = new JComboBox<>();

    public SettingsWindow(JDE instance) {
        this.instance = instance;
        this.logger = this.instance.getLogger();
        this.storageManager = this.instance.getStorageManager();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        panel.add(buildSettings(), BorderLayout.CENTER);
        panel.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(panel);

        setMinimumSize(new Dimension(1024, 1024)); // temporary dimension size

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setTitle("Settings");
        //this.instance.readLogo(ImageIcons.LOGO).ifPresent(this::setIconImage);
        setupKeybinds();
        pack();
        setLocationRelativeTo(null);

        load();
    }

    private void setupKeybinds() {
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "run");
        getRootPane().getActionMap().put("run", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {}
        });
    }

    private void load() {
        final Settings settings = this.instance.getSettings();
        this.zoomLevel.setValue(settings.zoomLevel);
        this.marginLine.setValue(settings.marginLinePosition);
        this.visibleWhitespace.setSelected(settings.visibleWhitespace);
        this.theme.setSelectedItem(settings.theme);
        this.logger.info("Settings loaded");
    }

    private void save() {
        final Settings settings = this.instance.getSettings();
        settings.zoomLevel = (int) this.zoomLevel.getValue();
        settings.marginLinePosition = (int) this.marginLine.getValue();
        settings.visibleWhitespace = this.visibleWhitespace.isSelected();
        settings.theme = (String) this.theme.getSelectedItem();
        this.instance.getStorageManager().setSettings(settings);
        this.logger.info("Settings saved");

        JOptionPane.showMessageDialog(this, "Please restart or re-open the project for the changes to be applied :)");
    }

    private JPanel buildSettings() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(row("Zoom Level", this.zoomLevel));
        panel.add(Box.createVerticalStrut(8));
        panel.add(row("Margin Line", this.marginLine));
        panel.add(Box.createVerticalStrut(8));
        panel.add(row("Visible Whitespace", this.visibleWhitespace));
        panel.add(Box.createVerticalGlue());
        panel.add(row("System Theme", this.theme));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel buildButtons() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.add(button("Save", _ -> save()));
        panel.add(button("Close", _ -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING))));
        return panel;
    }

    private JPanel row(final String text, final JComponent option) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel(text);
        label.setLabelFor(option);
        row.add(label, BorderLayout.WEST);
        row.add(option, BorderLayout.EAST);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(512, row.getPreferredSize().height));
        return row;
    }

    private JButton button(final String text, final ActionListener listener) {
        return button(text, Color.lightGray, listener);
    }

    private JButton button(final String text, final Color colour, final ActionListener listener) {
        JButton button = new JButton(text);
        button.setForeground(colour);
        button.addActionListener(listener);
        return button;
    }
}
