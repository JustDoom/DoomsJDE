package com.imjustdoom.doomsjde.filetypes;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MarkdownReader implements ReadableType {
    @Override
    public String[] recognisedFileTypes() {
        return new String[]{"md"};
    }

    @Override
    public String getSyntaxHighlighter() {
        return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
    }

    @Override
    public String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
