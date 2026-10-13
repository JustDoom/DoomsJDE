package com.imjustdoom.doomsjde.ui.objects;

import org.jspecify.annotations.NullMarked;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

@NullMarked
public final class Island extends JPanel {

    public Island withBorder(final Color color, final int thickness) {
        setBorder(BorderFactory.createLineBorder(color, thickness, true));

        return this;
    }

    public Island withLabel(final Consumer<JLabel> consumer) {
        final JLabel label = new JLabel();

        consumer.accept(label);

        add(label);

        return this;
    }

    public Island withDimension(final int width, final int height) {
        final Dimension dimension = new Dimension(width, height);

        setMinimumSize(dimension);
        setPreferredSize(dimension);

        return this;
    }

    public Island withSeparator(final int view) {
        final JSeparator separator = new JSeparator(view);

        //separator.setOrientation(view);
        separator.setPreferredSize(new Dimension(12, 12));

        add(separator);

        return this;
    }

    @SuppressWarnings("MagicConstant")
    public Island withBoxLayout(int axis) {
        setLayout(new BoxLayout(this, axis));

        return this;
    }

    public Island withBorderLayout() {
        setLayout(new BorderLayout());

        return this;
    }

    public Island withButton(final Consumer<JButton> consumer, final String layout) {
        final JButton button = new JButton();

        consumer.accept(button);

        add(button, layout);

        return this;
    }

    public Island withButton(final Consumer<JButton> consumer) {
        return withButton(consumer, "");
    }

    public Island withIsland(final Consumer<Island> consumer) {
        consumer.accept(this);

        return this;
    }
}