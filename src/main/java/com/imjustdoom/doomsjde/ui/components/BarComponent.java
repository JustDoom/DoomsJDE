package com.imjustdoom.doomsjde.ui.components;

import com.imjustdoom.doomsjde.api.JDE;
import com.imjustdoom.doomsjde.api.constants.Colours;
import com.imjustdoom.doomsjde.api.enums.ImageIcons;
import com.imjustdoom.doomsjde.ui.objects.Island;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BarComponent extends Island { //todo() make this a real reusable component

    private final JDE instance;
    private final JToolBar bar;

    public BarComponent(final JDE instance) {
        this.instance = instance;
        this.bar = new JToolBar();
    }

    public BarComponent init() {
        //todo() settings support for floatable
        this.bar.setFloatable(false);

        this.bar.setRollover(true);

        return this;
    }

    public BarComponent withButton(final String text, final Color color, final ActionListener listener) {
        this.bar.add(button(text, color, listener));

        return this;
    }

    public BarComponent withButton(final String text, final ActionListener listener) {
        return this.withButton(text, Colours.NICE_GREEN, listener);
    }

    public JToolBar build() {
        return this.bar;
    }

    private JButton button(final ImageIcons icon, final int width, final int height, final ActionListener listener) {
        final JButton button = new JButton(new ImageIcon(this.instance.readLogo(icon).get().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING)));

        button.addActionListener(listener);

        return button;
    }

    private JButton button(final String text, final Color colour, final ActionListener listener) {
        final JButton button = new JButton(text); //todo() button wrapper

        button.setForeground(colour);
        button.addActionListener(listener);

        return button;
    }
}