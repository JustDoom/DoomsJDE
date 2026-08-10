package com.imjustdoom.doomsjde.filetypes;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DockerfileReader implements ReadableType {
    @Override
    public String[] recognisedFileTypes() {
        return new String[]{"dockerfile"};
    }

    @Override
    public String getSyntaxHighlighter() {
        return SyntaxConstants.SYNTAX_STYLE_DOCKERFILE;
    }

    @Override
    public String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
