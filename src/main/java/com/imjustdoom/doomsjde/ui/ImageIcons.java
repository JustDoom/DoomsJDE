package com.imjustdoom.doomsjde.ui;

public enum ImageIcons {
    LOGO("icon");

    private final String path;

    ImageIcons(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
