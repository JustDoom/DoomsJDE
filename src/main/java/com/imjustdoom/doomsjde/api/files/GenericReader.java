package com.imjustdoom.doomsjde.api.files;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GenericReader implements ReadableType {
    private final String[] fileTypes;
    private final String syntaxHighlighter;

    public GenericReader() {
        this(SyntaxConstants.SYNTAX_STYLE_NONE);
    }

    public GenericReader(String syntaxHighlighter, String... fileTypes) {
        this.fileTypes = fileTypes;
        this.syntaxHighlighter = syntaxHighlighter;
    }

    @Override
    public String[] recognisedFileTypes() {
        return this.fileTypes;
    }

    @Override
    public String getSyntaxHighlighter() {
        return this.syntaxHighlighter;
    }

    @Override
    public String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
