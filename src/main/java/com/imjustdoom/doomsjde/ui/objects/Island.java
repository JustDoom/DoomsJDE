package com.imjustdoom.doomsjde.ui.objects;

import org.jspecify.annotations.NullMarked;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

@NullMarked
public final class Island extends JPanel {

    public Island setEtchedBorder(final String title) {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));

        return this;
    }

    public Island setEtchedBorder() {
        setBorder(BorderFactory.createEtchedBorder());

        return this;
    }

    public Island setLabel(final Consumer<JLabel> consumer) {
        final JLabel label = new JLabel();

        consumer.accept(label);

        add(label);

        return this;
    }

    public Island setButton(final Consumer<JButton> consumer) {
        final JButton button = new JButton();

        consumer.accept(button);

        add(button);

        return this;
    }

    public Island setDimension(final int width, final int height) {
        final Dimension dimension = new Dimension(width, height);

        setMinimumSize(dimension);
        setPreferredSize(dimension);

        return this;
    }
}