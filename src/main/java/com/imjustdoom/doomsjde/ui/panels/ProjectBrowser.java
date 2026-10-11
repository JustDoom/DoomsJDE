package com.imjustdoom.doomsjde.ui.panels;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.data.DataHandler;
import com.imjustdoom.doomsjde.ui.objects.Island;
import org.jspecify.annotations.NonNull;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.logging.Logger;

public class ProjectBrowser extends JFrame {

    private final DataHandler handler;
    private final Logger logger;
    private final JDE instance;

    public ProjectBrowser(@NonNull final JDE instance) {
        this.instance = instance;
        this.handler = this.instance.getDataHandler();
        this.logger = this.instance.getLogger();
    }

    public ProjectBrowser init() {
        final JPanel panel = new JPanel(new BorderLayout());

        final Island island = new Island();

        island.setDimension(256, 0).setEtchedBorder("Navigation Island")
                .setButton(builder -> {
                    builder.setText("Projects");
                    builder.setAlignmentX(0.5f);
                    builder.setAlignmentY(0.5f);
                })
                .setButton(builder -> {
                    builder.setText("Settings");

                    builder.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                    builder.setAlignmentX(Component.LEFT_ALIGNMENT);
                });

        // 4. Add sub-panels to specific regions of the main layout
        add(island, BorderLayout.WEST);

        setTitle("Welcome to JDE!");

        this.instance.readLogo("icon.png").ifPresent(this::setIconImage);

        setMinimumSize(new Dimension(740, 512));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();

        setLocationRelativeTo(null);

        return this;
    }
}