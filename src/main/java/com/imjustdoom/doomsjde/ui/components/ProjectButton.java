package com.imjustdoom.doomsjde.ui.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ProjectButton {

    private EventHandler<ActionEvent> event = _ -> {};

    private double maxWidth = 0.0;

    private double prefHeight = 0.0;
    private double minHeight = 0.0;
    private double maxHeight = 0.0;

    private String text = "N/A";

    public ProjectButton withText(final String text) {
        this.text = text;

        return this;
    }

    public ProjectButton withMaxWidth(final double maxWidth) {
        this.maxWidth = maxWidth;

        return this;
    }

    public ProjectButton withMaxHeight(final double maxHeight) {
        this.maxHeight = maxHeight;

        return this;
    }

    public ProjectButton withPrefHeight(final double prefHeight) {
        this.prefHeight = prefHeight;

        return this;
    }

    public ProjectButton withMinHeight(final double minHeight) {
        this.minHeight = minHeight;

        return this;
    }

    public ProjectButton withAction(final EventHandler<ActionEvent> event) {
        this.event = event;

        return this;
    }

    public Button build() {
        final Button button = new Button(this.text);

        button.setOnAction(this.event);

        if (this.maxWidth > 0.0) {
            button.setMaxWidth(this.maxWidth);
        }

        if (this.maxHeight > 0.0) {
            button.setMaxHeight(this.maxHeight);
        }

        if (this.minHeight > 0.0) {
            button.setMinHeight(this.minHeight);
        }

        if (this.prefHeight > 0.0) {
            button.setPrefHeight(this.prefHeight);
        }

        return button;
    }
}