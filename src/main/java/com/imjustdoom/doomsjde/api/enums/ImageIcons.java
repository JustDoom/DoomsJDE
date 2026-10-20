package com.imjustdoom.doomsjde.api.enums;

public enum ImageIcons {

    LOGO("icon.png");

    private final String path;

    ImageIcons(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
